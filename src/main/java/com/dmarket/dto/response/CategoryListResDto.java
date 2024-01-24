package com.dmarket.dto.response;

import com.dmarket.domain.product.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListResDto {
    private Long categoryId;
    private Integer categoryDepth;
    private String categoryName;
    private List<CategoryListResDto> child;

    public CategoryListResDto(Category category){
        this.categoryId = category.getCategoryId();
        this.categoryDepth = category.getCategoryDepth();
        this.categoryName = category.getCategoryName();
        this.child = category.getChild().stream().map(CategoryListResDto::new).collect(Collectors.toList());
    }

}
