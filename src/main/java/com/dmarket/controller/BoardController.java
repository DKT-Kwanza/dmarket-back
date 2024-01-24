package com.dmarket.controller;

import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.NoticeListDto;
import com.dmarket.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping(value = "/notice")
    public ResponseEntity<?> getNotices() {
        try {
            List<NoticeListDto> notices = boardService.getAllNotices().stream()
                    .map(notice -> new NoticeListDto(
                            notice.getNoticeId(),
                            notice.getNoticeTitle(),
                            notice.getNoticeContents(),
                            notice.getNoticeCreatedDate()))
                    .collect(Collectors.toList());

            // response build
            CMResDto<List<NoticeListDto>> response = CMResDto.<List<NoticeListDto>>builder()
                    .code(200)
                    .msg("공지사항 목록 불러오기 완료")
                    .data(notices)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Handle invalid requests
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
//        } catch (AuthenticationException e) {
//            // Handle authentication errors
//            log.warn("유효하지 않은 인증" + e.getMessage());
//            return new ResponseEntity<>(CMResDto.builder()
//                    .code(401).msg("유효하지 않은 인증").build(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error retrieving notices: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder()
                    .code(500)
                    .msg("서버 내부 오류")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}