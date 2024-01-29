package com.dmarket.controller;

import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.Notice;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.FaqListResDto;
import com.dmarket.dto.response.NoticeResDto;
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
    private static final int BOARD_PAGE_SIZE = 10;
    @GetMapping("/notice")
    public ResponseEntity<?> getNotices(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        pageNo = pageNo > 0 ? pageNo - 1 : 0;

        Page<Notice> noticesPage = boardService.getAllNotices(PageRequest.of(pageNo, BOARD_PAGE_SIZE));
        Page<NoticeResDto> mappedNotices = boardService.mapToNoticeResDto(noticesPage);

        CMResDto<?> response = CMResDto.successDataRes(mappedNotices);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/faq")
    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {
        pageNo = pageNo > 0 ? pageNo - 1 : 0;

        Page<Faq> faqsPage = boardService.getAllFaqs(PageRequest.of(pageNo, BOARD_PAGE_SIZE));
        Page<FaqListResDto> mappedFaqs = boardService.mapToFaqListResDto(faqsPage);

        CMResDto<?> response = CMResDto.successDataRes(mappedFaqs);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}