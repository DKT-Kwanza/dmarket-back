package com.dmarket.domain.user;

import com.dmarket.constant.MileageContents;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mileage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mileageId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer remainMileage;

    @Column(nullable = false)
    private Integer changeMileage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MileageContents mileageInfo;

    @Column(nullable = false)
    private LocalDateTime mileageDate;


    @Builder
    public Mileage(Long userId, Integer remainMileage, Integer changeMileage, MileageContents mileageInfo) {
        this.userId = userId;
        this.remainMileage = remainMileage;
        this.changeMileage = changeMileage;
        this.mileageInfo = mileageInfo;
        this.mileageDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public static Mileage mileageAutoCharge(Long userId){
        return Mileage.builder()
                .userId(userId)
                .remainMileage(1200000)
                .changeMileage(1200000)
                .mileageInfo(MileageContents.AUTO_CHARGE)
                .build();
    }
}
