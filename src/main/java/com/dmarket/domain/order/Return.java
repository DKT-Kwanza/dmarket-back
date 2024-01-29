package com.dmarket.domain.order;

import com.dmarket.constant.ReturnState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

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

    public void updateReturnState(ReturnState returnState) {
        this.returnState = returnState;
        this.returnUpdatedDate = LocalDateTime.now();
    }

    @Builder
    public Return(Long orderDetailId, ReturnState returnState, String returnReason){
        this.orderDetailId = orderDetailId;
        this.returnState = returnState;
        this.returnReason = returnReason;
        this.returnRequestDate = LocalDateTime.now().withNano(0);
        this.returnUpdatedDate = LocalDateTime.now().withNano(0);
    }
}
