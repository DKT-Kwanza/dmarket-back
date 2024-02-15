package com.dmarket.service;

import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.Notice;
import com.dmarket.dto.response.FaqResDto;
import com.dmarket.dto.response.NoticeResDto;
import com.dmarket.repository.board.FaqRepository;
import com.dmarket.repository.board.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;

    private static final int BOARD_PAGE_SIZE = 10;

    // 공지사항 목록 조회
    public Page<Notice> getAllNotices(int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, BOARD_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "noticeCreatedDate"));
        return noticeRepository.findAll(pageable);
    }

    public Page<NoticeResDto> mapToNoticeResDto(Page<Notice> noticesPage) {
        return noticesPage.map(no -> new NoticeResDto(no));
    }

    // FAQ 목록 조회
    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public List<FaqResDto.FaqListResDto> mapToFaqListResDto(List<Faq> faqs) {
        return faqs.stream().map(faq -> new FaqResDto.FaqListResDto(
                faq.getFaqId(),
                faq.getFaqType(),
                faq.getFaqQuestion(),
                faq.getFaqAnswer()
        )).collect(Collectors.toList());
    }

    // 페이지 번호 유효성 검사 메소드
    public int pageVaildation(int page) {
        page = page > 0 ? page - 1 : page;
        return page;
    }
}
