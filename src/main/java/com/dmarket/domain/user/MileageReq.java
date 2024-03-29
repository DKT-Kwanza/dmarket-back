package com.dmarket.domain.user;

import com.dmarket.constant.MileageContents;
import com.dmarket.constant.MileageReqState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MileageReq {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mileageReqId;

    private Long userId;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime mileageReqDate;

    @Column(nullable = false)
    private Integer mileageReqAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MileageContents mileageReqReason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MileageReqState mileageReqState;

    private LocalDateTime mileageReqUpdatedDate;  //상태 변경 일자

    @Builder
    public MileageReq(Long userId, Integer mileageReqAmount, MileageContents mileageReqReason, MileageReqState mileageReqState){
        this.userId = userId;
        this.mileageReqAmount = mileageReqAmount;
        this.mileageReqReason = mileageReqReason;
        this.mileageReqState = mileageReqState;
        this.mileageReqDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void updateState(MileageReqState mileageReqState){
        this.mileageReqState = mileageReqState;
        this.mileageReqUpdatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
