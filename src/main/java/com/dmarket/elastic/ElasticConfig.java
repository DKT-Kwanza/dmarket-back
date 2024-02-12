package com.dmarket.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:application-elastic.properties")
@Configuration
public class ElasticConfig {
//    @Value("${elastic.serverUrl}")
//    private String serverUrl;
//    @Value("${elastic.apiKey}")
//    private String apiKey;

//    RestClient restClient = RestClient
//            .builder(HttpHost.create(serverUrl))
//            .setDefaultHeaders(new Header[]{
//                    new BasicHeader("Authorization", "ApiKey " + apiKey)
//            })
//            .build();
//
//    ElasticsearchTransport transport = new RestClientTransport(
//            restClient, new JacksonJsonpMapper());
//
//    ElasticsearchClient client = new ElasticsearchClient(transport);
}
