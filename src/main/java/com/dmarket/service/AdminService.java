package com.dmarket.service;

import com.dmarket.constant.InquiryType;
import com.dmarket.constant.OrderDetailState;
import com.dmarket.domain.order.Order;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.dto.common.InquiryDetailsDto;
import com.dmarket.constant.FaqType;
import com.dmarket.constant.ReturnState;
import com.dmarket.domain.order.Return;
import com.dmarket.domain.product.Category;
import com.dmarket.domain.product.Product;
import com.dmarket.domain.product.ProductImgs;
import com.dmarket.domain.product.ProductOption;
import com.dmarket.dto.common.OrderDetailStateCountsDto;
import com.dmarket.dto.request.ProductListDto;
import com.dmarket.dto.request.StockReqDto;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.order.ReturnRepository;
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
import com.dmarket.dto.common.ProductOptionDto;
import com.dmarket.dto.request.OptionReqDto;
import com.dmarket.dto.request.ProductReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.repository.board.*;
import com.dmarket.repository.product.ProductReviewRepository;
import com.dmarket.repository.user.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final FaqRepository faqRepository;
    private final ReturnRepository returnRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImgsRepository productImgsRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;
    private final WishlistRepository wishlistRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;

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
    // 반품 상태 업데이트
    @Transactional
    public void updateReturnState(Long returnId, ReturnState returnState) {
        Return returnEntity = returnRepository.findById(returnId)
                .orElseThrow(() -> new IllegalArgumentException("해당 returnId가 존재하지 않습니다. returnId: " + returnId));
        returnEntity.updateReturnState(returnState);
    }

    // 신상품 등록
    @Transactional
    public void saveProductList(List<ProductListDto> productList) {
        System.out.println("1.");
        for (ProductListDto productitem : productList) {
            System.out.println("********");
            // CategoryId 가져오기
            Long categoryId = getCategoryByCategoryName(productitem.getCategoryName());

            // Product 저장
            Product savedProduct = saveProduct(productitem, categoryId);

            // OptionList 저장
            saveProductOptions(savedProduct.getProductId(), productitem.getOptionList());

            // ProductImgs 저장
            saveProductImgs(savedProduct.getProductId(), productitem.getImgList());
        }
    }
    @Transactional
    public Long getCategoryByCategoryName(String categoryName) {
        System.out.println("2.");
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category != null) {
            return category.getCategoryId();
        }
        return null;
    }
    @Transactional
    public Product saveProduct(ProductListDto productitem, Long categoryId) {
        System.out.println("3.");
        Product newProduct = Product.builder()
                .categoryId(categoryId)
                .productBrand(productitem.getBrand())
                .productName(productitem.getProductName())
                .productPrice(Integer.parseInt(productitem.getProductPrice().replace(",", "")))
                .productSalePrice(Integer.parseInt(productitem.getProductSalePrice().replace(",", "")))
                .productDescription(productitem.getProductDes())
                .build();

        return productRepository.save(newProduct);
    }
    @Transactional
    public void saveProductOptions(Long productId, List<ProductListDto.Option> optionList) {
        System.out.println("4.");
        for (ProductListDto.Option option : optionList) {
            ProductOption newOption = ProductOption.builder()
                    .productId(productId)
                    .optionName(option.getOptionName())
                    .optionValue(option.getOptionValue())
                    .optionQuantity(option.getOptionQuantity())
                    .build();

            productOptionRepository.save(newOption);
        }
    }
    @Transactional
    public void saveProductImgs(Long productId, List<String> imgAddresses) {
        System.out.println("5.");
        for (String imgAddress : imgAddresses) {
            ProductImgs productImgs = ProductImgs.builder()
                    .productId(productId)
                    .imgAddress(imgAddress)
                    .build();

            productImgsRepository.save(productImgs);
        }
    }


    //문의 목록 조회(카테고리별)
    @Transactional
    public Page<InquiryListResDto> getAllInquiriesByType(InquiryType inquiryType, Pageable pageable) {
        return inquiryRepository.findByInquiryType(inquiryType, pageable);
    }

    //문의 삭제
    @Transactional
    public boolean deleteInquiry(Long inquiryId) {
        Optional<Inquiry> inquiryOptional = inquiryRepository.findById(inquiryId);

        if (inquiryOptional.isPresent()) {
            inquiryRepository.deleteById(inquiryId);
            return true;
        } else {
            return false; // 삭제 대상이 없음
        }
    }


    //문의 답변 등록
    @Transactional
    public InquiryReply createInquiryReply(InquiryReply inquiryReply) {
        return inquiryReplyRepository.save(inquiryReply);
    }

    public InquiryDetailsDto getInquiryDetails(Long inquiryId) {
        InquiryDetailsDto inquiryDetailsDto = InquiryDetailsDto.builder()
                .inquiryId(inquiryId)
                .inquiryTitle("Sample Title")
                .inquiryContents("Sample Contents")
                .inquiryType("Sample Type")
                .inquiryStatus(false)
                .inquiryWriter("Sample Writer")
                .inquiryImg("www.example.com/sample.png")
                .inquiryCreateDate("2024-01-07 13:48:00")
                .inquiryReplyContents("Sample Reply Contents")
                .build();

        return inquiryDetailsDto;
        // 나중에 수정할게요..
    }

    // 문의 답변 삭제
    @Transactional
    public boolean deleteInquiryReply(Long inquiryReplyId) {
        Optional<InquiryReply> inquiryReplyOptional = inquiryReplyRepository.findById(inquiryReplyId);

        if (inquiryReplyOptional.isPresent()) {
            inquiryReplyRepository.deleteById(inquiryReplyId);
            return true;
        } else {
            return false; // 삭제 대상이 없을 때
        }
    }


    // 상품 재고 추가
    @Transactional
    public void addProductStock(StockReqDto stockReqDto) {
        try {
            Long productId = stockReqDto.getProductId();
            Long optionId = stockReqDto.getOptionId();
            Integer addCount = stockReqDto.getAddCount();

            if (productId == null || optionId == null || addCount == null || addCount <= 0) {
                throw new IllegalArgumentException("상품 정보 및 추가 수량을 확인하세요.");
            }

            Optional<ProductOption> optionalProductOption = productOptionRepository.findById(optionId);

            if (optionalProductOption.isPresent()) {
                ProductOption productOption = optionalProductOption.get();

                productOption.setOptionQuantity(productOption.getOptionQuantity() + addCount);
                productOptionRepository.save(productOption);
            } else {
                throw new IllegalArgumentException("상품 옵션을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("상품 재고 추가 중 오류 발생", e);
        }
    }
    // 상품 재고 추가 RESPONSE
    public ProductInfoOptionResDto getProductInfoWithOption(Long productId) {
        try {
            List<ProductInfoOptionResDto> productDetails = productRepository.findProductDetails(productId);

            if (!productDetails.isEmpty()) {
                ProductInfoOptionResDto productDetail = productDetails.get(0);

                return ProductInfoOptionResDto.builder()
                        .productId(productDetail.getProductId())
                        .productBrand(productDetail.getProductBrand())
                        .productName(productDetail.getProductName())
                        .optionId(productDetail.getOptionId())
                        .optionValue(productDetail.getOptionValue())
                        .optionName(productDetail.getOptionName())
                        .productImg(productDetail.getProductImg())
                        .optionQuantity(productDetail.getOptionQuantity())
                        .build();
            } else {
                throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("상품 정보 조회 중 오류 발생", e);
        }
    }


    // 배송 목록 조회
    public OrderDetailStateCountsDto getOrderDetailStateCounts() {
        return orderRepository.getOrderDetailStateCounts();
    }

    public List<OrderListResDto> getOrdersByStatus(String status) {
        try {
            OrderDetailState orderStatus = OrderDetailState.valueOf(status);
            List<OrderDetail> orderDetails = orderDetailRepository
                    .findByOrderDetailStateOrderByOrderDetailUpdatedDateDesc(orderStatus);

            List<Long> productIds = orderDetails.stream()
                    .map(OrderDetail::getProductId)
                    .collect(Collectors.toList());

            List<Product> products = productRepository.findAllById(productIds);
            List<ProductOption> productOptions = productOptionRepository.findOptionsByProductIdIn(productIds);
            List<ProductImgs> productImgs = productImgsRepository.findAllByProductIdIn(productIds);

            List<Order> orders = orderDetails.stream()
                    .map(orderDetail -> orderRepository.findByOrderId(orderDetail.getOrderId())
                            .orElseThrow(() -> new RuntimeException("Order not found for OrderDetailId: " + orderDetail.getOrderDetailId())))
                    .collect(Collectors.toList());

            return mapOrderDetailsToDto(orderDetails, products, productOptions, productImgs, orders);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 주문 상태: " + status);
        } catch (Exception e) {
            throw new RuntimeException("주문 목록 조회 중 오류 발생", e);
        }
    }


    private List<OrderListResDto> mapOrderDetailsToDto(List<OrderDetail> orderDetails,
                                                       List<Product> products,
                                                       List<ProductOption> productOptions,
                                                       List<ProductImgs> productImgs,
                                                       List<Order> orders) {
        Map<Long, List<ProductOption>> optionsByProductId = productOptions.stream()
                .collect(Collectors.groupingBy(ProductOption::getProductId));

        Map<Long, List<ProductImgs>> productImgsByProductId = productImgs.stream()
                .collect(Collectors.groupingBy(ProductImgs::getProductId));

        return orderDetails.stream()
                .map(orderDetail -> {
                    Product product = getProductById(products, orderDetail.getProductId());
                    List<ProductOption> options = optionsByProductId.get(orderDetail.getProductId());
                    List<ProductImgs> imgs = productImgsByProductId.get(orderDetail.getProductId());

                    if (product != null) {
                        String optionName = "SampleOptionName";
                        String optionValue = "SampleOptionValue";
                        String imgAddress = "https://sample-image.com/sample.png";

                        if (options != null && !options.isEmpty()) {
                            ProductOption productOption = options.get(0);
                            optionName = productOption.getOptionName();
                            optionValue = productOption.getOptionValue();
                        }

                        if (imgs != null && !imgs.isEmpty()) {
                            imgAddress = imgs.get(0).getImgAddress();
                        }

                        // Find the corresponding order for the current orderDetail
                        Order order = orders.stream()
                                .filter(o -> o.getOrderId().equals(orderDetail.getOrderId()))
                                .findFirst()
                                .orElse(null);


                        return new OrderListResDto(
                                orderDetail.getOrderId(),
                                order != null ? order.getOrderDate() : null,
                                orderDetail.getOrderDetailId(),
                                orderDetail.getProductId(),
                                orderDetail.getOptionId(),
                                optionName,
                                optionValue,
                                product.getProductBrand(),
                                product.getProductName(),
                                imgAddress,
                                orderDetail.getOrderDetailCount(),
                                orderDetail.getOrderDetailState().toString());
                    } else {
                        return new OrderListResDto();
                    }
                })
                .collect(Collectors.toList());
    }

    private Product getProductById(List<Product> products, Long productId) {
        return products.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
    // ---배송 목록 조회 ---









}
