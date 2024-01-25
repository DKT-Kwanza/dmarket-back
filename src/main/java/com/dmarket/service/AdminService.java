package com.dmarket.service;

import com.dmarket.constant.FaqType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmarket.domain.board.*;
import com.dmarket.domain.product.*;
import com.dmarket.domain.user.*;
import com.dmarket.dto.common.ProductOptionDto;
import com.dmarket.dto.request.OptionReqDto;
import com.dmarket.dto.request.ProductReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.repository.board.*;
import com.dmarket.repository.product.CategoryRepository;
import com.dmarket.repository.product.ProductImgsRepository;
import com.dmarket.repository.product.ProductOptionRepository;
import com.dmarket.repository.product.ProductRepository;
import com.dmarket.repository.product.ProductReviewRepository;
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

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImgsRepository productImgsRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;
    private final WishlistRepository wishlistRepository;

    @Transactional
    public void deleteUserByUserId(Long userId) {
        userRepository.deleteByUserId(userId);
    }

    public List<UserResDto> getUsersFindByDktNum(Integer userDktNum) {
        return userRepository.getUsersFindByDktNum(userDktNum);
    }

    public Page<NoticeResDto> getNotices(Pageable pageable) {
        return noticeRepository.getNotices(pageable);
    }

    @Transactional
    public Page<NoticeListResDto> postNotice(Long userId, String noticeTitle, String noticeContents, Pageable pageable) {
        Notice notice = Notice.builder()
                .userId(userId)
                .noticeTitle(noticeTitle)
                .noticeContents(noticeContents)
                .build();
        noticeRepository.save(notice);

        Page<Notice> noticesPage = noticeRepository.findAll(pageable);
        return noticesPage.map(no -> new NoticeListResDto(
                no.getNoticeId(),
                no.getNoticeTitle(),
                no.getNoticeContents(),
                no.getNoticeCreatedDate()));
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
    @Transactional
    public void updateProduct(ProductReqDto productReqDto) {
        Long categoryId = categoryRepository.findByCategoryName(productReqDto.getCategoryName()).getCategoryId();

        // Product 엔티티를 찾거나 없으면 새로 생성 (업데이트 로직)
        Product product = productRepository.findById(productReqDto.getProductId())
                .orElseGet(() -> Product.builder()
                        .categoryId(categoryId)
                        .productBrand(productReqDto.getProductBrand())
                        .productName(productReqDto.getProductName())
                        .productPrice(productReqDto.getProductPrice())
                        .productSalePrice(productReqDto.getProductSalePrice())
                        .productDescription(productReqDto.getProductDes())
                        .build());
        productRepository.save(product);

        // Product 필드 업데이트
        productRepository.updateProductDetails(
                productReqDto.getProductId(),
                categoryId,
                productReqDto.getProductBrand(),
                productReqDto.getProductName(),
                productReqDto.getProductPrice(),
                productReqDto.getProductSalePrice(),
                productReqDto.getProductDes());

        // ProductOption 리스트 처리 전, 기존 옵션 삭제
        if (!productReqDto.getOptionList().isEmpty()) {
            productOptionRepository.deleteByProductId(productReqDto.getProductId());
            if (productReqDto.getOptionList() != null) {
                for (OptionReqDto option : productReqDto.getOptionList()) {
                    ProductOption productOption = ProductOption.builder()
                            .productId(productReqDto.getProductId())
                            .optionName(option.getOptionName())
                            .optionValue(option.getOptionValue())
                            .optionQuantity(option.getOptionQuantity())
                            .build();
                    productOptionRepository.save(productOption);

                }
            }
        }

        // ProductImgs 리스트 처리
        if (!productReqDto.getImgList().isEmpty()) {
            productImgsRepository.deleteByProductId(productReqDto.getProductId());
            if (productReqDto.getImgList() != null) {
                for (String imgAddress : productReqDto.getImgList()) {
                    ProductImgs productImgs = ProductImgs.builder()
                            .productId(productReqDto.getProductId())
                            .imgAddress(imgAddress)
                            .build();
                    productImgsRepository.save(productImgs);

                }
            }
        }
    }

    // 상품 상세 정보 조회
    public ProductInfoResDto getProductInfo(Long productId, Long userId) {
        // 싱품 정보 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        // 상품의 카테고리 depth 1, depth2 조회 후 합치기
        Category category = categoryRepository.findByCategoryId(product.getCategoryId());
        String productCategory = category.getParent().getCategoryName() + " / " + category.getCategoryName();
        // 상품의 리뷰 개수 조회
        Long reviewCnt = productReviewRepository.countByProductId(productId);
        // 사용자가 위시리스트에 등록한 상품인지 확인
        Boolean isWish = wishlistRepository.existsByUserIdAndProductId(userId, productId);
        // 상품 옵션 목록, 옵션별 재고 조회
        List<ProductOptionDto> opts = productOptionRepository.findOptionsByProductId(productId);
        // 상품 이미지 목록 조회
        List<String> imgs = productImgsRepository.findAllByProductId(productId);
        // DTO 생성 및 반환
        return new ProductInfoResDto(product, productCategory, reviewCnt, isWish, opts, imgs);
    }

    public Page<AdminReviewsResDto> getProductReviews(Pageable pageable) {
        return productReviewRepository.getProductReviews(pageable);
    }
}
