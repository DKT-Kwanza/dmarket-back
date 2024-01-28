package com.dmarket.dto.common;

import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductImgs;
import com.dmarket.domain.product.ProductOption;
import com.dmarket.domain.user.Cart;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CartCommonDto {
    @Data
    @Getter
    @NoArgsConstructor
    public static class CartListDto {
        private Long cartId;
        private Long productId;
        private String productBrand;
        private String productName;
        private String productImg;
        private String productOption;
        private Integer productCount;
        private Integer productTotalSalePrice;

        public CartListDto(Cart cart, Product product, ProductOption productOption, ProductImgs productImgs) {
            this.cartId = cart.getCartId();
            this.productId = product.getProductId();
            this.productBrand = product.getProductBrand();
            this.productName = product.getProductName();
            this.productImg = productImgs.getImgAddress();
            this.productOption = productOption.getOptionName();
            this.productCount = cart.getCartCount();
            this.productTotalSalePrice = cart.getCartCount() * product.getProductSalePrice();
           }

        // getters and setters
    }
}
