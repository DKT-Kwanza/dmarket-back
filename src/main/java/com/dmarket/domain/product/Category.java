package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = true)
    private Long categoryParentId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Integer categoryDepth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryParentId", referencedColumnName = "categoryId", insertable=false, updatable=false)
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> child = new ArrayList<>();

    public class RedisCacheKey {

        public static final String CATEGORY_LIST = "categoryList";
        public static final String PRODUCT_LIST = "productList";
    }
}
