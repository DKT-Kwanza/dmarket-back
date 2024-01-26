package com.dmarket.service;

import com.dmarket.constant.FaqType;
import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.dto.common.ProductOptionDto;
import com.dmarket.dto.common.ProductOptionListDto;
import com.dmarket.repository.product.CategoryRepository;
import com.dmarket.repository.product.ProductImgsRepository;
import com.dmarket.repository.product.ProductOptionRepository;
import com.dmarket.repository.product.ProductRepository;
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
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final ProductImgsRepository productImgsRepository;
    private final CategoryRepository categoryRepository;

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

    // FAQ 등록
    @Transactional
    public Long postFaq(FaqType faqType, String faqQuestion, String faqAnswer) {
        Faq faq = Faq.builder()
                .faqType(faqType)
                .faqQuestion(faqQuestion)
                .faqAnswer(faqAnswer)
                .build();
        Faq savedFaq = faqRepository.save(faq);
        Long faqId = savedFaq.getFaqId();

        return faqId;
    }

    // 옵션 삭제
    @Transactional
    public void deleteOptionByOptionId(Long optionId) {
        productOptionRepository.deleteByOptionId(optionId);
    }

    // 상품 목록 조회
    public List<ProductListAdminResDto> getProductListByCateogryId(Long categoryId) {
//        Product product = categoryRepository.findProductsByCategoryId(categoryId);
//        Category category = categoryRepository.findByCategoryId(categoryId);
//        List<ProductOptionListDto> options = categoryRepository.findOptionsByCategoryId(categoryId);
//        List<String> imgs = categoryRepository.findImgsByCategoryId(categoryId);
//
//        return new ProductListAdminResDto(product, category, options, imgs);
        List<Product> products = categoryRepository.findProductsByCategoryId(categoryId);
        Category category = categoryRepository.findByCategoryId(categoryId);
        List<ProductOptionListDto> options = categoryRepository.findOptionsByCategoryId(categoryId);
        List<String> imgs = categoryRepository.findImgsByCategoryId(categoryId);

        // 제품 목록을 처리하고 DTO 목록을 만듭니다.
        List<ProductListAdminResDto> result = new ArrayList<>();
        for (Product product : products) {
            result.add(new ProductListAdminResDto(product, category, options, imgs));
        }

        return result;
    }
}
