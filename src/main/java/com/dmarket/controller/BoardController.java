package com.dmarket.controller;

import com.dmarket.domain.board.Notice;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.NoticeListResDto;
import com.dmarket.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    // 공지사항 목록 조회
    @GetMapping(value = "/notice")
    public ResponseEntity<?> getNotices(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                        @RequestParam(required = false, value = "size", defaultValue = "10") int pageSize) {
        try {
            if (pageNo < 0 || pageSize <= 0) {
                // Invalid input for page number or size, return 400 Bad Request
                return new ResponseEntity<>(CMResDto.builder()
                        .code(400)
                        .msg("유효하지 않은 페이지 또는 크기")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            Page<Notice> noticesPage = boardService.getAllNotices(PageRequest.of(pageNo, pageSize));

            CMResDto<Page<NoticeListResDto>> response = CMResDto.<Page<NoticeListResDto>>builder()
                    .code(200)
                    .msg("공지사항 목록 불러오기 완료")
                    .data(noticesPage.map(notice -> new NoticeListResDto(
                            notice.getNoticeId(),
                            notice.getNoticeTitle(),
                            notice.getNoticeContents(),
                            notice.getNoticeCreatedDate())))
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