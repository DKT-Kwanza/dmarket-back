package com.dmarket.controller;

import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.Notice;
import com.dmarket.dto.response.CMResDto;
import com.dmarket.dto.response.FaqResDto;
import com.dmarket.dto.response.NoticeResDto;
import com.dmarket.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> getNotices(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        Page<Notice> noticesPage = boardService.getAllNotices(pageNo);
        Page<NoticeResDto> mappedNotices = boardService.mapToNoticeResDto(noticesPage);
        CMResDto<?> response = CMResDto.successDataRes(mappedNotices);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/faq")
    public ResponseEntity<?> getFaqs(@RequestParam(required = false, value = "page", defaultValue = "0") int pageNo) {

        Page<Faq> faqsPage = boardService.getAllFaqs(pageNo);
        Page<FaqResDto.FaqListResDto> mappedFaqs = boardService.mapToFaqListResDto(faqsPage);

        CMResDto<?> response = CMResDto.successDataRes(mappedFaqs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}