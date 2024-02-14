package com.dmarket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
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
import com.dmarket.dto.response.ProductResDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PropertySource("classpath:application-elastic.properties")
public class ElasticsearchService {

    private final String serverUrl = "https://96cf190a5133454f97f8cf99af05d321.us-central1.gcp.cloud.es.io:443";
    private final String apiKey = "dE5TSW5vMEJmNHV1eUxBVlVSR3Q6OUxrY2Q4T2tSVHFIT0lhcFRHR1BaZw==";
    private static final int PAGE_SIZE = 16;

//    @Value("${elastic.serverUrl}")
//    private String serverUrl;
//    @Value("${elastic.apiKey}")
//    private String apiKey;

    RestClient restClient = RestClient
            .builder(HttpHost.create(serverUrl))
            .setDefaultHeaders(new Header[]{
                    new BasicHeader("Authorization", "ApiKey " + apiKey)
            })
            .build();

    ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

    ElasticsearchClient client = new ElasticsearchClient(transport);


    public ProductResDto.ProductSearchListResDto getElasticSearchProducts(int pageNo, String query, String sorter,
                                                                          Integer minPrice, Integer maxPrice, Float star) throws IOException {

        // 별점 필터링
        Query byRating = RangeQuery.of(r -> r
                .field("product_rating")
                .gte(JsonData.of(star))
        )._toQuery();
        // product_sale_price 필터링
        Query byPrice = RangeQuery.of(r -> r
                .field("product_sale_price")
                .gte(JsonData.of(minPrice))
                .lte(JsonData.of(maxPrice))
        )._toQuery();
        // 검색어 필터링 기본
        List<String> fields = Arrays.asList("product_name", "product_brand", "product_description");
        Query byName = MultiMatchQuery.of(m -> m
                .fields(fields)
                .query(query)
        )._toQuery();
        // 검색어 필터링 nori
        List<String> norifields = Arrays.asList("product_name.nori", "product_brand.nori", "product_description.nori");
        Query byNoriname = MultiMatchQuery.of(m -> m
                .fields(norifields)
                .query(query)
        )._toQuery();
        // 검색어 필터링 ngram
        List<String> ngramfields = Arrays.asList("product_name.ngram", "product_brand.ngram", "product_description.ngram");
        Query byNgramName = MultiMatchQuery.of(m -> m
                .fields(ngramfields)
                .query(query)
        )._toQuery();
        // 검색
        SearchResponse<ProductDocument> response = client.search(s -> s
                        .index("new-product")
                        .from(pageNo * PAGE_SIZE)
                        .size(PAGE_SIZE)
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
                                        .field(sorter)
                                        .order(SortOrder.Desc)
                                )
                        )
                ,
                ProductDocument.class
        );
    int totalValues = (int)response.hits().total().value();
    int totalPages = getTotalPages(totalValues);

    List<ProductCommonDto.ProductSearchListDto> productList = new ArrayList<>();
    for (Hit<ProductDocument> hit: response.hits().hits()) {

        productList.add(new ProductCommonDto.ProductSearchListDto(
                hit.source().getProduct_id(),
                hit.source().getProduct_brand(),hit.source().getProduct_name(),
                hit.source().getImgs_enriched().getImg_address(),
                hit.source().getProduct_sale_price(),
                hit.source().getProduct_discount_rate(),
                hit.source().getProduct_rating(),
                hit.source().getReview_enriched()==null ? 0 : Long.valueOf(hit.source().getReview_enriched().size())));
        }
    //리뷰 많은 순 정렬 따로 분리
//    if(sorter.equals("review_count")){
//        // 간선을 퀵정렬로 비용의 오름차순 정렬
//        quickSort(productList, 0, productList.size());
//        }
    return new ProductResDto.ProductSearchListResDto(totalPages,productList);
    }


    //퀵 정렬 알고리즘
//    public static void quickSort(List<ProductResDto.ProductListResDto> list, int start, int end) {
//        if (start >= end) {
//            return;  //원소가 1개인 경우
//        }
//
//        int pivot = start;  //pivot값은 첫번째 원소
//        int i = start + 1;  //시작점 - 왼쪽부터 큰 값을 찾을 때 시작하는 인덱스
//        int j = end;  //도착점 - 오른쪽부터 작은 값을 찾을 때 시작하는 인덱스
//        int temp;  //수를 바꿀 때 임시 변수
//
//        while (i <= j) { // 엇갈릴 때까지 반복
//            while (arr[i] >= arr[pivot]) { //pivot보다 더 큰 값을 만날 때까지
//                i++;
//            }
//            while (arr[j] <= arr[pivot] && j > start) { //pivot보다 더 작은 값을 만날 때까지
//                j--;
//            }
//            if (i > j) { //현재 엇갈린 상태면 pivot값과 교체
//                temp = arr[j];
//                arr[j] = arr[pivot];
//                arr[pivot] = temp;
//            } else { //엇갈리지 않았다면 i와 j를 교체
//                temp = arr[i];
//                arr[i] = arr[j];
//                arr[j] = temp;
//            }
//        }
//
//        //분할된 왼쪽과 오른쪽 둘다 퀵 정렬 수행
//        quickSort(arr, start, j - 1);
//        quickSort(arr, j + 1, end);
//    }

    public int getTotalPages(int totalValues){
        int totalPages;
        if(totalValues > 0 && totalValues < 10 ){
            totalPages = 1;
        }else{
            totalPages=totalValues/10;
        }
        return totalPages;
    }
}
