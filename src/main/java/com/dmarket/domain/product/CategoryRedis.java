package com.dmarket.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@RedisHash(value = "category")
@Getter
public class CategoryRedis {

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
    private CategoryRedis parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<CategoryRedis> child = new ArrayList<>();

    public CategoryRedis(Long categoryId, Long categoryParentId, String categoryName, Integer categoryDepth) {
        this.categoryId = categoryId;
        this.categoryParentId = categoryParentId;
        this.categoryName = categoryName;
        this.categoryDepth = categoryDepth;
    }
}
