package com.dmarket.domain.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
//@Document(indexName = "product-ngram")
//@Mapping(mappingPath = "elastic/comment-mapping.json")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReviewDocument {

    @Id
    @Field(name = "review_id", type = FieldType.Long)
    private Long review_id;
    @Field(name = "product_id", type = FieldType.Long)
    private Long product_id;
}