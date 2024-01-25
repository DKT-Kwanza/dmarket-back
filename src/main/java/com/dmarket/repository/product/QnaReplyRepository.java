package com.dmarket.repository.product;

import com.dmarket.domain.product.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {
}
