package com.dmarket.service;

import com.dmarket.constant.FaqType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmarket.domain.board.*;
import com.dmarket.dto.response.*;
import com.dmarket.repository.board.*;
import com.dmarket.repository.user.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;


    @Transactional
    public void deleteUserByUserId(Long userId) {
        userRepository.deleteByUserId(userId);
    }

    public List<UserResDto> getUsersFindByDktNum(Integer userDktNum) {
        return userRepository.getUsersFindByDktNum(userDktNum);
    }

    public List<NoticeResDto> getNotices() {
        return noticeRepository.getNotices();
    }
    @Transactional
    public void postNotice(Long userId, String noticeTitle, String noticeContents) {
        Notice notice = Notice.builder()
                .userId(userId)
                .noticeTitle(noticeTitle)
                .noticeContents(noticeContents)
                .build();
        noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNoticeByNoticeId(Long noticeId) {
        noticeRepository.deleteByNoticeId(noticeId);
    }
    // FAQ 조회
    public Page<Faq> getAllFaqs(FaqType faqType, Pageable pageable) {
        return faqRepository.findFaqType(faqType, pageable);
    }
    public Page<FaqListResDto> mapToFaqListResDto(Page<Faq> faqsPage) {
        return faqsPage.map(faq -> new FaqListResDto(
                faq.getFaqId(),
                faq.getFaqType(),
                faq.getFaqQuestion(),
                faq.getFaqAnswer()
        ));
    }
    // FAQ 삭제
    @Transactional
    public void deleteFaqByFaqId(Long faqId) {
        faqRepository.deleteByFaqId(faqId);
    }


}
