package com.dmarket.repository.board;

import com.dmarket.domain.board.QnaReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaReplyRepository extends JpaRepository<QnaReply, Long> {
}
