package com.dmarket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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

    public SearchResponse<ProductDocument> getElasticSearchProducts(String query) throws IOException {
        List<String> arr = new ArrayList<>();
        arr.add("product_name"); arr.add("product_brand"); arr.add("product_description");
        SearchResponse<ProductDocument> response = client.search(s -> s
                        .index("product-ngram").size(5000)
                        .query(q -> q.multiMatch(qs ->
                                qs.fields(arr).query(query))
                        ),
                ProductDocument.class
        );
        return response;
    }
}
