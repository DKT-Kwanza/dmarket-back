package com.dmarket.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
//import org.springframework.data.elasticsearch.annotations.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
//@Document(indexName = "sourcedb.dmarket.product")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ProductDocument {

//    @Id
//    @Field(name = "product_id", type = FieldType.Long)
    private Long product_id;
//    @Field(name = "category_id", type = FieldType.Long)
    private Long category_id;
//    @Field(name = "product_brand", type = FieldType.Text)
    private String product_brand;
//    @Field(name = "product_name", type = FieldType.Text)
    private String product_name;
//    @Field(name = "product_price", type = FieldType.Integer)
    private Integer product_price;
//    @Field(name = "product_sale_price", type = FieldType.Integer)
    private Integer product_sale_price;
//    @Field(name = "product_description", type = FieldType.Text)
    private String product_description;
//    @Field(name = "product_rating", type = FieldType.Float)
    private Float product_rating;
//    @Field(name = "product_created_date", type = FieldType.Long)
    private Date product_created_date;
}