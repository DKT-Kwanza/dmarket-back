package com.dmarket.repository.board;

import com.dmarket.domain.board.Notice;
import com.dmarket.dto.response.NoticeResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT new com.dmarket.dto.response.NoticeResDto(n) FROM Notice n ORDER BY n.noticeId DESC")
    Page<NoticeResDto> getNotices(Pageable pageable);

    void deleteByNoticeId(@Param("noticeId") Long noticeId);
}
