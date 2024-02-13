package com.dmarket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.dmarket.domain.product.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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

//    public SearchResponse<ProductDocument> getElasticSearchProducts(String query) throws IOException {
//        List<String> arr = new ArrayList<>();
//        arr.add("product_name"); arr.add("product_brand"); arr.add("product_description");
//        SearchResponse<ProductDocument> response = client.search(s -> s
//                        .index("product-ngram").size(5000)
//                        .query(q -> q.multiMatch(qs ->
//                                qs.fields(arr).query(query))
//                        ),
//                ProductDocument.class
//        );
//        return response;
//    }


    public SearchResponse<ProductDocument> getElasticSearchProducts(int pageNo, String query, String sorter, Integer minPrice, Integer maxPrice, Float star) throws IOException {
        // 페이지네이션
        int pageSize = 10;
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
                .from(pageNo * pageSize)
                .size(pageSize)
                .query(q -> q
                        .bool(b -> b
                                .must(byName)
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
        return response;
    }
//    SearchResponse<ProductDocument> response = client.search(s -> s
//            .index("product-ngram")
//            .from(from)
//            .size(size)
//            .query(q -> q
//                .bool(b -> b
//                    .should(sh -> sh
//                        .multiMatch(m -> m
//                            .query(query)
//                            .fields(fields)
//                        )
//                    )
//                    .must(mu -> mu
//                        .range(r -> r
//                            .field("product_sale_price")
//                            .gte(minPrice)
//                            .lte(maxPrice)
//                        ),
//                        mu -> mu
//                            .range(r -> r
//                                .field("product_rating")
//                                .gte(minRating)
//                            )
//                    )
//                    .minimumShouldMatch(1)
//                )
//            )
//            .aggregation("product_ids", a -> a
//                .terms(t -> t
//                    .field("product_id")
//                    .size(5000)
//                    .subAggregation("review_count", sa -> sa
//                        .cardinality(c -> c
//                            .field("review_enriched.review_id")
//                        )
//                    )
//                    .subAggregation("paging", sa -> sa
//                        .bucketSort(bs -> bs
//                            .from(0)
//                            .size(10)
//                        )
//                    )
//                )
//            )
//            .sort(so -> so
//                .score(SortOrder.DESC)
//            ),
//            so -> so
//                .field("product_created_date", SortOrder.DESC)
//            )
//            .trackTotalHits(true),
//        ProductDocument.class
//    );


}
