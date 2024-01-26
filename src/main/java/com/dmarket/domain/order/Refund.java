package com.dmarket.domain.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    private Long returnId;

    private Boolean refundState;

    public Refund(Long returnId, Boolean refundState) {
        this.returnId = returnId;
        this.refundState = refundState;
    }
}
