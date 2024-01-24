package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListDto {
    private Long noticeId;
    private String noticeTitle;
    private String noticeContents;
    private LocalDateTime noticeCreatedDate;
}
