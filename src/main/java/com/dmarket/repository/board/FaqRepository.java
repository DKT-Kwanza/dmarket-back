package com.dmarket.repository.board;

import com.dmarket.constant.FaqType;
import com.dmarket.domain.board.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    @Query("select f from Faq f where f.faqType = :faqType")
    Page<Faq> findFaqType(@Param("faqType") FaqType faqType, Pageable pageable);

    // faq 삭제
    void deleteByFaqId(@Param("faqId") Long faqId);
}
