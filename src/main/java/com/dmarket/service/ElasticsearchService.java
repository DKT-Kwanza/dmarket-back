package com.dmarket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public SearchResponse<ObjectNode> getElasticSearchProducts(String query) throws IOException {
        List<String> arr = new ArrayList<>();
        arr.add("product_name"); arr.add("product_brand"); arr.add("product_description");
        SearchResponse<ProductDocument> response = client.search(s -> s
                        .index("product-ngram").size(5000)
                        .query(q -> q.multiMatch(qs ->
                                qs.fields(arr).query(query))
                        ),
                ProductDocument.class
        );
        for (Hit<ProductDocument> hit: response.hits().hits()) {
            ProductDocument product = hit.source();
            System.out.println("Product Name: " + product.getProduct_name());
        }

//        SphereDistanceAggregate reviews = response
//                .aggregations().get("product_ids")
//                ._custom()
//                .to(SphereDistanceAggregate.class);
//        for (Bucket bucket : reviews.buckets()) {
//            System.out.println(bucket.key+ bucket.docCount);
//            System.out.println("review Count: "+ bucket.reviewCount);
//        }

//        JsonData reviews = response
//                .aggregations().get("product_ids")
//                ._custom();
//
//
//        JsonArray buckets = reviews.toJson()
//                .asJsonObject()
//                .getJsonArray("buckets");
//
//        for (JsonValue item : buckets) {
//            JsonObject bucket = item.asJsonObject();
//            double key = bucket.getJsonNumber("key").doubleValue();
//            double docCount = bucket.getJsonNumber("doc_count").longValue();
//            System.out.println(key + docCount);
//        }


        SearchResponse<ObjectNode> test = client.search(s -> s
                        .index("product-ngram").size(5000)
                        .query(q -> q.multiMatch(qs ->
                                qs.fields(arr).query(query))
                        ),
                ObjectNode.class
        );

        List<ProductResDto.ProductListResDto> productList = new ArrayList<>();

        for (Hit<ObjectNode> hit: test.hits().hits()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timeInDate =hit.source().getProduct_created_date();
            String timeInFormat = sdf.format(timeInDate);

            System.out.println(hit.source().getProduct_id());
            System.out.println(timeInFormat);

            comments.add(new ProductResDto.ProductListResDto(hit.source().getProduct_id(),
                    hit.source().getProduct_brand(),hit.source().getProduct_name(),
                    hit.source(),
                    hit.source().getTitle(), hit.source().getContents(),
                    hit.source().getSummary(),
                    timeInFormat,
                    hit.source().getViews(),
                    hit.source().getComment_size()));
        }
        return productList;
    }

    public static class SphereDistanceAggregate {
        private final List<Bucket> buckets;
        @JsonCreator
        public SphereDistanceAggregate(
                @JsonProperty("buckets") List<Bucket> buckets
        ) {
            this.buckets = buckets;
        }

        public List<Bucket> buckets() {
            return buckets;
        };
    }

    public static class Bucket {
        private final double key;
        private final double docCount;
        private final Object reviewCount;
        @JsonCreator
        public Bucket(
                @JsonProperty("key") double key,
                @JsonProperty("doc_count") double docCount,
                @JsonProperty("review_count") double reviewCount) {
            this.key = key;
            this.docCount = docCount;
            this.reviewCount = reviewCount;
        }
        public double key() {
            return key;
        }
        public double docCount() {
            return docCount;
        }
        public Object reviewCount() {
            return reviewCount;
        }
    }
}
