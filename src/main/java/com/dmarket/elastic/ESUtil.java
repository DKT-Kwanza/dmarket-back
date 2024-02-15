package com.dmarket.elastic;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import lombok.val;

import java.util.List;
import java.util.function.Supplier;

public class ESUtil {
    public static Supplier<Query> supplierQueryForMultiMatch(String key, List<String> fields){
        return () ->Query.of(q->q.multiMatch(multiMatchQuery(key, fields)));
    }

    public static MultiMatchQuery multiMatchQuery(String key, List<String> fields){
        val multiMatch = new MultiMatchQuery.Builder();
        return multiMatch.query("*"+key+"*").fields(fields).build();
    }

    // 별점 필터링 쿼리
    public static Query filterByRating(Float star){
        return RangeQuery.of(r -> r
                .field("product_rating")
                .gte(JsonData.of(star))
        )._toQuery();
    }

    // product_sale_price 필터링
    public static Query fileterByPrice(Integer minPrice, Integer maxPrice){
        return RangeQuery.of(r -> r
                .field("product_sale_price")
                .gte(JsonData.of(minPrice))
                .lte(JsonData.of(maxPrice))
        )._toQuery();
    }
    // 검색어 필터링
    public static Query filterBySearch(List<String> fields, String query){
        return MultiMatchQuery.of(m -> m
                .fields(fields)
                .query(query)
        )._toQuery();
    }
    // 검색어 필터링 nori
    public static Query filterByNoriSearch(List<String> noriFields, String query){
        return MultiMatchQuery.of(m -> m
                .fields(noriFields)
                .query(query)
        )._toQuery();
    }
    // 검색어 필터링 ngram
    public static Query filterByNgramSearch(List<String> ngramFields, String query){
        return MultiMatchQuery.of(m -> m
                .fields(ngramFields)
                .query(query)
        )._toQuery();
    }
}
