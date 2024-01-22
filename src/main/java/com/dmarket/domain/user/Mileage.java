package com.dmarket.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mileage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mileageId;

    private Long userId;

    @Column(nullable = false)
    private Integer mileageRemain;

    @Column(nullable = false)
    private Integer mileageUse;

    @Column(nullable = false)
    private String mileageInfo;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime mileageDate;
}
