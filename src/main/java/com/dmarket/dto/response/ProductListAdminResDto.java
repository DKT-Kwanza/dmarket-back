package com.dmarket.dto.response;

import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.ProductOptionDto;
import com.dmarket.dto.common.ProductOptionListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListAdminResDto {
    private Long productId;
    private String productBrand;
    private String productName;
    private Integer productSalePrice;
    private String productCategory;
    private List<ProductOptionListDto> optionList;
    private LocalDateTime productRegistDate;
    private List<String> imgList;

    public ProductListAdminResDto(Product product, Category category, List<ProductOptionListDto> options, List<String> imgs){
        this.productId = product.getProductId();
        this.productBrand = product.getProductBrand();
        this.productName = product.getProductName();
        this.productSalePrice = product.getProductSalePrice();
        this.productCategory = category.getCategoryName();
        this.optionList = options;
        this.productRegistDate = product.getProductCreatedDate();
        this.imgList = imgs;
    }

}
