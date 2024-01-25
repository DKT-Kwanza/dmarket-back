package com.dmarket.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmarket.domain.product.QnaReply;

@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {
}
