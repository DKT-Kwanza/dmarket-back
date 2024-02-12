package com.dmarket.elastic;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
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
}
