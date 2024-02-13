package com.dmarket.domain.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
@Document(indexName = "product-ngram")
//@Mapping(mappingPath = "elastic/comment-mapping.json")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ProductDocument {

    @Id
    @Field(name = "product_id", type = FieldType.Long)
    private Long product_id;
    @Field(name = "category_id", type = FieldType.Long)
    private Long category_id;
    @Field(name = "product_brand", type = FieldType.Text)
    private String product_brand;
    @Field(name = "product_name", type = FieldType.Text)
    private String product_name;
    @Field(name = "product_price", type = FieldType.Integer)
    private Integer product_price;
    @Field(name = "product_sale_price", type = FieldType.Integer)
    private Integer product_sale_price;
    @Field(name = "product_description", type = FieldType.Text)
    private String product_description;
    @Field(name = "product_rating", type = FieldType.Float)
    private Float product_rating;
    @Field(name = "product_created_date", type = FieldType.Long)
    private Date product_created_date;
    @Field(name = "imgs_enriched", type = FieldType.Object)
    private ImgDocument imgs_enriched;
    @Field(name = "review_enriched", type = FieldType.Nested)
    private ArrayNode reviews;
}