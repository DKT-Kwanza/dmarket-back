package com.dmarket.service;

import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.Notice;
import com.dmarket.repository.board.FaqRepository;
import com.dmarket.repository.board.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;

    public Page<Notice> getAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public Page<Faq> getAllFaqs(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }
}
