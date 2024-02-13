package com.dmarket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
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

    public List<ProductResDto.ProductListResDto> getElasticSearchProducts(String query,  Integer minPrice, Integer maxPrice, Float star) throws IOException {

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
        // 검색어 필터링
        List<String> fields = Arrays.asList("product_name", "product_brand", "product_description");
        Query byName = MultiMatchQuery.of(m -> m
                .fields(fields)
                .query(query)
        )._toQuery();
        // 검색
        SearchResponse<ProductDocument> response = client.search(s -> s
                .index("product-ngram")
                .query(q -> q
                        .bool(b -> b
                                .must(byName)
                                .must(byPrice)
                                .must(byRating)
                                )
                        ),ProductDocument.class
        );

        List<ProductResDto.ProductListResDto> productList = new ArrayList<>();
        for (Hit<ProductDocument> hit: response.hits().hits()) {

            productList.add(new ProductResDto.ProductListResDto(
                    hit.source().getProduct_id(),
                    hit.source().getProduct_brand(),hit.source().getProduct_name(),
                    hit.source().getImgs_enriched().getImg_address(),
                    hit.source().getProduct_sale_price(),
                    hit.source().getProduct_discount_rate(),
                    hit.source().getProduct_rating(),
                    hit.source().getReview_enriched()==null ? 0 : Long.valueOf(hit.source().getReview_enriched().size())));
        }
        return productList;
    }
}
