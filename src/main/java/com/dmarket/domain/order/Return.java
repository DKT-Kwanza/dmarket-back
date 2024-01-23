package com.dmarket.domain.order;

import com.dmarket.constant.ReturnState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "returns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Return {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    private Long orderDetailId;

    @Enumerated(EnumType.STRING)
    private ReturnState returnState;

    @Column(columnDefinition = "TEXT")
    private String returnReason;

    private LocalDateTime returnRequestDate;
    private LocalDateTime returnUpdatedDate;
}
