package com.dmarket.domain.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
//@Document(indexName = "product-ngram")
//@Mapping(mappingPath = "elastic/comment-mapping.json")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ImgDocument {

    @Id
    @Field(name = "img_id", type = FieldType.Long)
    private Long img_id;
    @Field(name = "img_address", type = FieldType.Text)
    private String img_address;
    @Field(name = "product_id", type = FieldType.Long)
    private Long product_id;
}
