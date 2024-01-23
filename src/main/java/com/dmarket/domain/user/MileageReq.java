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
public class MileageReq {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mileageReqId;

    private Long userId;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime mileageReqDate;

    @Column(nullable = false)
    private Integer mileageReqAmount;

    @Column(columnDefinition="TEXT")
    private String mileageReqReason;

    @Column(nullable = false)
    private String mileageReqState;

    private LocalDateTime mileageReqUpdatedDate;  //상태 변경 일자
}
