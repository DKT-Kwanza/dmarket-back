package com.dmarket.repository.board;

import com.dmarket.domain.board.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.dmarket.dto.response.NoticeResDto;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT new com.dmarket.dto.response.NoticeResDto(n) FROM Notice n")
    public List<NoticeResDto> getNotices();

    void deleteByNoticeId(@Param("noticeId") Long noticeId);
}
