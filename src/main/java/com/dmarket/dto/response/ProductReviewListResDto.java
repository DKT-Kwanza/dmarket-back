package com.dmarket.dto.response;

import com.dmarket.dto.common.ProductDto;
import com.dmarket.dto.common.ProductReviewDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductReviewListResDto {
    private Long productId;
    private Float productRating;
    private Long productReviewCount;
    private List<ProductReviewDto> reviewList;

    public ProductReviewListResDto(ProductDto product, List<ProductReviewDto> reviewList){
        this.productId = product.getProductId();
        this.productRating = product.getProductRating();
        this.productReviewCount = product.getProductReviewCount();
        this.reviewList = reviewList;
    }
}
