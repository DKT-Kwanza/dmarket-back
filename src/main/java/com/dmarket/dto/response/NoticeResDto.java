package com.dmarket.dto.response;

import java.time.LocalDateTime;

import com.dmarket.domain.board.Notice;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class NoticeResDto {
    private Long noticeId;
    private LocalDateTime noticeCreatedDate;
    private String noticeTitle;
    private String noticeContents;


    public NoticeResDto(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.noticeCreatedDate = notice.getNoticeCreatedDate();
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContents = notice.getNoticeContents();
    }

    
}
