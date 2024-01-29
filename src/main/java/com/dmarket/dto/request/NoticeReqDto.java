package com.dmarket.dto.request;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class NoticeReqDto {
    @NotNull
    private Long userId;
    @NotEmpty
    private String noticeTitle;
    @NotEmpty
    private String noticeContents;
}
