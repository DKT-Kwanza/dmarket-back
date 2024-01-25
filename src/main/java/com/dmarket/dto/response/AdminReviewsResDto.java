package com.dmarket.dto.response;
import com.dmarket.domain.order.*;
import com.dmarket.domain.product.*;
import com.dmarket.domain.user.User;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Data
@Getter
@NoArgsConstructor
public class AdminReviewsResDto {
    private Long productId;
    private Long reviewId;
    private String reviewWriter;
    private String optionName;
    private Integer reviewRating;
    private String reviewContents;
    private LocalDateTime reviewCreatedDate;


    public AdminReviewsResDto(ProductReview productReview, ProductOption productOption, User user){
        this.productId = productReview.getProductId();
        this.reviewId = productReview.getReviewId();
        this.reviewWriter = user.getUserName();
        this.optionName = productOption.getOptionName();
        this.reviewRating = productReview.getReviewRating();
        this.reviewContents = productReview.getReviewContents();
        this.reviewCreatedDate = productReview.getReviewCreatedDate();
    }

}
