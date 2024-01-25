package com.dmarket.controller;

import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.Notice;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.FaqListResDto;
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

    @GetMapping("/notice")
    public ResponseEntity<?> getNotices(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                        @RequestParam(required = false, value = "size", defaultValue = "10") int pageSize) {
        try {
            if (pageNo < 0 || pageSize <= 0) {
                return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 페이지 또는 크기").build(), HttpStatus.BAD_REQUEST);
            }

            Page<Notice> noticesPage = boardService.getAllNotices(PageRequest.of(pageNo, pageSize));
            Page<NoticeListResDto> mappedNotices = boardService.mapToNoticeListResDto(noticesPage);

            CMResDto<Page<NoticeListResDto>> response = CMResDto.<Page<NoticeListResDto>>builder().code(200).msg("공지사항 목록 불러오기 완료").data(mappedNotices).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error retrieving notices: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/faq")
    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo,
                                     @RequestParam(required = false, value = "size", defaultValue = "10") int pageSize) {
        try {
            if (pageNo < 0 || pageSize <= 0) {
                return new ResponseEntity<>(CMResDto.builder()
                        .code(400).msg("유효하지 않은 페이지 또는 크기").build(), HttpStatus.BAD_REQUEST);
            }

            Page<Faq> faqsPage = boardService.getAllFaqs(PageRequest.of(pageNo, pageSize));
            Page<FaqListResDto> mappedFaqs = boardService.mapToFaqListResDto(faqsPage);

            CMResDto<Page<FaqListResDto>> response = CMResDto.<Page<FaqListResDto>>builder().code(200).msg("FAQ 조회 성공").data(mappedFaqs).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 요청 메시지:" + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(400).msg("유효하지 않은 요청 메시지").build(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error retrieving FAQs: " + e.getMessage());
            return new ResponseEntity<>(CMResDto.builder().code(500).msg("서버 내부 오류").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}