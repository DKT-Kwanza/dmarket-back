package com.dmarket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.dmarket.domain.document.ProductDocument;
import com.dmarket.dto.common.ProductCommonDto;
import com.dmarket.dto.response.OrderResDto;
import com.dmarket.dto.response.ProductResDto;
import com.dmarket.dto.response.ReviewResDto;
import com.dmarket.elastic.ESUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElasticsearchService {
    @Value("${elasticsearch.serverUrl}")
    private String serverUrl;
    @Value("${elasticsearch.apiKey}")
    private String apiKey;

    private static final int PAGE_SIZE = 16;

    private RestClient restClient;
    private ElasticsearchTransport transport;
    private ElasticsearchClient client;

    private static List<ProductCommonDto.ProductSearchListDto> productSearchList = new ArrayList<>();

    @PostConstruct
    public void init() {
        restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();

        transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
    }

    public ProductResDto.ProductSearchListResDto getElasticSearchProducts(int pageNo, String query, String sorter,
                                                                          Integer minPrice, Integer maxPrice, Float star) throws IOException {

        List<String> fields = Arrays.asList("product_name^2", "product_brand^2", "product_description^1");
        List<String> noriFields = Arrays.asList("product_name.nori^2", "product_brand.nori^2", "product_description.nori^1");
        List<String> ngramFields = Arrays.asList("product_name.ngram^1.5", "product_brand.ngram^1.5", "product_description.ngram^0.5");
        // 별점 필터링
        Query byRating = ESUtil.filterByRating(star);
        // product_sale_price 필터링
        Query byPrice = ESUtil.fileterByPrice(minPrice, maxPrice);
        // 검색어 필터링
        Query byName = ESUtil.filterBySearch(fields, query);
        // 검색어 필터링 nori
        Query byNoriname = ESUtil.filterByNoriSearch(noriFields, query);
        // 검색어 필터링 ngram
        Query byNgramName = ESUtil.filterByNgramSearch(ngramFields, query);

        SortOptions sort1 = new SortOptions.Builder().field(f -> f.field("_score").order(SortOrder.Desc)).build();
        SortOptions sort2 = new SortOptions.Builder().field(f -> f.field(sorter).order(SortOrder.Desc)).build();
        List<SortOptions> sortList = new ArrayList<SortOptions>();
        sortList.add(sort2); sortList.add(sort1);

        // 검색
        SearchResponse<ProductDocument> response = client.search(s -> s
                        .index("new-product")
                        .from(pageNo * PAGE_SIZE)
                        .size(PAGE_SIZE)
                        .minScore(10.0)
                        .query(q -> q
                                .bool(b -> b
                                        .should(byName)
                                        .should(byNoriname)
                                        .should(byNgramName)
                                        .minimumShouldMatch("1")
                                        .must(byPrice)
                                        .must(byRating)
                                )
                        )
                        .sort(sortList),ProductDocument.class
        );
    int totalValues = (int)response.hits().total().value();
    int totalPages = getTotalPages(totalValues);
    List<ProductCommonDto.ProductSearchListDto> productList = new ArrayList<>();

    return new ProductResDto.ProductSearchListResDto(totalPages, getResponse(response, productList));
    }

    public ProductResDto.ProductSearchListResDto getElasticSearchProductsReviewORder(int pageNo, String query, String sorter,
                                                                          Integer minPrice, Integer maxPrice, Float star) throws IOException {
        List<String> fields = Arrays.asList("product_name^2", "product_brand^2", "product_description^1");
        List<String> noriFields = Arrays.asList("product_name.nori^2", "product_brand.nori^2", "product_description.nori^1");
        List<String> ngramFields = Arrays.asList("product_name.ngram^1.5", "product_brand.ngram^1.5", "product_description.ngram^0.5");
        // 별점 필터링
        Query byRating = ESUtil.filterByRating(star);
        // product_sale_price 필터링
        Query byPrice = ESUtil.fileterByPrice(minPrice, maxPrice);
        // 검색어 필터링
        Query byName = ESUtil.filterBySearch(fields, query);
        // 검색어 필터링 nori
        Query byNoriname = ESUtil.filterByNoriSearch(noriFields, query);
        // 검색어 필터링 ngram
        Query byNgramName = ESUtil.filterByNgramSearch(ngramFields, query);

        // 검색
        SearchResponse<ProductDocument> response = client.search(s -> s
                .index("new-product")
                .size(5000)
                .minScore(10.0)
                .query(q -> q
                        .bool(b -> b
                                .should(byName)
                                .should(byNoriname)
                                .should(byNgramName)
                                .minimumShouldMatch("1")
                                .must(byPrice)
                                .must(byRating)
                        )
                )
                .sort(so -> so
                        .field(f -> f
                                .field("_score")
                                .order(SortOrder.Desc))
                ),ProductDocument.class
        );
        int totalValues = (int)response.hits().total().value();

        productSearchList = getResponse(response, productSearchList);
        quickSort(0,productSearchList.size()-1);

        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE);
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()),totalValues);
        Page<ProductCommonDto.ProductSearchListDto> productList = new PageImpl<>
                (productSearchList.subList(start,end), pageable, totalValues);
        productSearchList = new ArrayList<>();

        return new ProductResDto.ProductSearchListResDto(productList.getTotalPages(), productList.getContent());
    }


    public static void quickSort(int start, int end) {
        // start가 end보다 크거나 같다면 정렬할 원소가 1개 이하이므로 정렬하지 않고 반환
        if (start >= end)
            return;

        // 가장 왼쪽의 값을 pivot으로 지정하고 실제 비교 검사는 start+1 부터 시작
        int pivot = start;
        int lo = start + 1;
        int hi = end; // 정렬의 마지막 값 지정
        ProductCommonDto.ProductSearchListDto temp;

        // lo는 현재 부분배열의 왼쪽, hi는 오른쪽을 의미
        // lo가 hi보다 커질 경우 while문 종료
        while (lo <= hi) {
            // lo번째 간선의 비용이 pivot번째 간선의 비용보다 큰 값일 때까지 반복
            while (lo <= end && productSearchList.get(lo).getProductReviewCount() >= productSearchList.get(pivot).getProductReviewCount())
                lo++;
            // hi번째 간선의 비용이 pivot번째 간선의 비용보다 작은 값일 때까지 반복
            while (hi > start && productSearchList.get(hi).getProductReviewCount() <= productSearchList.get(pivot).getProductReviewCount())
                hi--;
            // 엇갈리면 hi간선과 피벗 index의 간선 교체
            if (lo > hi) {
                temp = productSearchList.get(hi);
                productSearchList.set(hi, productSearchList.get(pivot));
                productSearchList.set(pivot, temp);
            }
            // 엇갈리지 않으면 lo와 hi 간선 교체
            else {
                temp = productSearchList.get(lo);
                productSearchList.set(lo, productSearchList.get(hi));
                productSearchList.set(hi, temp);
            }
        }

        // 엇갈렸을 경우, 피벗값과 hi값을 교체한 후 해당 피벗을 기준으로 앞 뒤로 배열을 분할하여 정렬 반복 진행
        quickSort(start, hi - 1);
        quickSort(hi + 1, end);
    }

    public int getTotalPages(int totalValues){
        int totalPages;
        if(totalValues > 0 && totalValues < PAGE_SIZE ){
            totalPages = 1;
        }else{
            totalPages=totalValues/PAGE_SIZE;
        }
        return totalPages;
    }

    public List<ProductCommonDto.ProductSearchListDto> getResponse(SearchResponse<ProductDocument> response,
                                                                   List<ProductCommonDto.ProductSearchListDto> productList){

        for (Hit<ProductDocument> hit: response.hits().hits()) {
            productList.add(new ProductCommonDto.ProductSearchListDto(
                    hit.source().getProduct_id(),
                    hit.source().getProduct_brand(),hit.source().getProduct_name(),
                    hit.source().getImgs_enriched().getImg_address(),
                    hit.source().getProduct_sale_price(),
                    hit.source().getProduct_discount_rate(),
                    hit.source().getProduct_rating(),
                    hit.source().getReview_enriched()==null ? 0 : hit.source().getReview_enriched().size()));
        }
        return productList;
    }
}
