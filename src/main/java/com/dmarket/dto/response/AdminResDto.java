package com.dmarket.dto.response;

import com.dmarket.constant.Role;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductOption;
import com.dmarket.domain.product.ProductReview;
import com.dmarket.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminResDto {
    @Data
    @Getter
    @NoArgsConstructor
    public static class AdminReviewsResDto {
        private Long productId;
        private Long reviewId;
        private String reviewWriter;
        private String productName;
        private String optionName;
        private Integer reviewRating;
        private String reviewContents;
        private LocalDateTime reviewCreatedDate;


        public AdminReviewsResDto(ProductReview productReview, ProductOption productOption, User user, Product product){
            this.productId = productReview.getProductId();
            this.reviewId = productReview.getReviewId();
            this.reviewWriter = user.getUserName();
            this.productName = product.getProductName();
            this.optionName = productOption.getOptionName();
            this.reviewRating = productReview.getReviewRating();
            this.reviewContents = productReview.getReviewContents();
            this.reviewCreatedDate = productReview.getReviewCreatedDate();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManagerInfoDto {
        private Long userId;
        private String userName;
        private String userEmail;
        private String userRole; // Role Enum을 String으로 변환
        private String userJoinDate; // LocalDate를 String으로 변환

        public ManagerInfoDto(Long userId, String userName, String userEmail, Role userRole, LocalDateTime userJoinDate) {
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.userRole = userRole.name();
            this.userJoinDate = userJoinDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Getters and Setters 생략
    }
}
