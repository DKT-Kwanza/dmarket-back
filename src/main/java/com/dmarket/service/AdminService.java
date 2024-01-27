package com.dmarket.service;

import com.dmarket.domain.order.Order;
import com.dmarket.domain.order.OrderDetail;
import com.dmarket.domain.order.Refund;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import com.dmarket.domain.product.*;
import com.dmarket.domain.order.Return;
import com.dmarket.domain.board.*;
import com.dmarket.constant.*;
import com.dmarket.dto.common.*;
import com.dmarket.dto.request.*;
import com.dmarket.dto.request.ProductListDto;
import com.dmarket.dto.response.*;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.product.*;
import com.dmarket.repository.board.*;
import com.dmarket.repository.user.*;
import com.dmarket.repository.order.RefundRepository;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.ReturnRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MileageReqRepository mileageReqRepository;

    private final FaqRepository faqRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final ProductImgsRepository productImgsRepository;
    private final CategoryRepository categoryRepository;
    private final QnaRepository qnaRepository;
    private final QnaReplyRepository qnaReplyRepository;

    private final ReturnRepository returnRepository;
    private final ProductReviewRepository productReviewRepository;
    private final WishlistRepository wishlistRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;


    private static final int PAGE_POST_COUNT = 10;

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
    public Page<NoticeResDto> postNotice(Long userId, String noticeTitle, String noticeContents,
            Pageable pageable) {
        Notice notice = Notice.builder()
                .userId(userId)
                .noticeTitle(noticeTitle)
                .noticeContents(noticeContents)
                .build();
        noticeRepository.save(notice);

        Page<Notice> noticesPage = noticeRepository.findAll(pageable);
        return noticesPage.map(no -> new NoticeResDto(no));
    }

    // 마일리지 충전 요청 내역
    @Transactional
    public MileageReqListResDto getMileageRequests(Pageable pageable, String status, int pageNo){
        pageable = PageRequest.of(pageNo, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "mileageReqDate"));
        Page<MileageReqDto> dtos;
        if(status.equals("PROCESSING")){
            dtos = mileageReqRepository.findAllByProcessing(pageable);
        }else if(status.equals("PROCESSED")){
            dtos = mileageReqRepository.findAllByProcessed(pageable);
        }else{
            throw new IllegalArgumentException("올바르지 않은 경로입니다.");
        }
        List<MileageReqListDto> mileageRequests = dtos.getContent().stream()
                .map(MileageReqListDto::new).toList();

        return new MileageReqListResDto(dtos.getTotalPages(), mileageRequests);
    }

    // 마일리지 충전 요청 처리
    @Transactional
    public void approveMileageReq(Long mileageReqId, boolean request){
        MileageReq mileageReq = findMileageReqById(mileageReqId);
        if(request){
            mileageReq.updateState(MileageReqState.APPROVAL);
            User user = findUserById(mileageReq.getUserId());
            user.updateMileage(mileageReq.getMileageReqAmount());
        } else {
            mileageReq.updateState(MileageReqState.REFUSAL);
        }
    }

    @Transactional
    public void deleteNoticeByNoticeId(Long noticeId) {
        noticeRepository.deleteByNoticeId(noticeId);
    }

    //사용자 ID로 찾기
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));
    }

    //사용자 마일리지 요청 찾기
    public MileageReq findMileageReqById(Long mileageReqId) {
        return mileageReqRepository.findById(mileageReqId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 마일리지 요청 입니다."));
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
                faq.getFaqAnswer()));
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

    // 상품 QnA 조회
    public QnaListResDto getQnaList(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "qnaCreatedDate"));
        Page<QnaDto> qnaList = qnaRepository.findAllQna(pageable);
        return new QnaListResDto(qnaList.getTotalPages(), qnaList.getContent());
    }

    // 상품 QnA 상세(개별) 조회
    public QnaDetailResDto getQnADetail(Long qnaId) {
        return qnaRepository.findQnaAndReply(qnaId);
    }

    // 상품 QnA 답변 작성
    @Transactional
    public QnaDetailResDto createQnaReply(Long qnaId, String qnaReplyContents) {
        // QnA 존재 확인
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Qna"));

        // 답변 저장
        QnaReply qnaReply = QnaReply.builder()
                .qnaId(qnaId)
                .qnaReplyContents(qnaReplyContents)
                .build();
        qnaReplyRepository.save(qnaReply);

        // QnA 답변 상태 변경 -> 답변 대기
        qna.updateState(true);

        return qnaRepository.findQnaAndReply(qnaId);
    }

    // 상품 QnA 답변 삭제
    @Transactional
    public QnaDetailResDto deleteQnaReply(Long qnaReplyId){
        // QnA 번호 가져오기
        Long qnaId = qnaReplyRepository.findQnaIdByQnaReplyId(qnaReplyId);

        // QnA 존재 확인
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Qna"));

        // QnA 답변 삭제
        qnaReplyRepository.deleteById(qnaReplyId);

        // 답변 상태 변경 -> 답변 대기
        qna.updateState(false);

        return qnaRepository.findQnaAndReply(qnaId);
    }

    // 반품 상태 리스트
    public ReturnListResDto getReturns(String returnStatus, Pageable pageable) {
        ReturnState returnState = null;
        switch (returnStatus) {
            case "반품 요청":
                returnState = ReturnState.RETURN_REQUEST;
                break;
            case "수거중":
                returnState = ReturnState.COLLECT_ING;
                break;
            case "수거 완료":
                returnState = ReturnState.COLLECT_COMPLETE;
                break;
            default:
                throw new IllegalArgumentException("Unknown return state: " + returnStatus);
        }
        Page<ReturnDto> returnDto = returnRepository.getReturnsByReturnState(returnState, pageable);
        ReturnListResDto returnListResDto = returnRepository.getReturnsCount();
        returnListResDto.setReturnList(returnDto);
        return returnListResDto;

    }

    // 반품 상태 업데이트
    @Transactional
    public void updateReturnState(Long returnId, String returnState) {
        Return returnEntity = returnRepository.findById(returnId)
                .orElseThrow(() -> new IllegalArgumentException("해당 returnId가 존재하지 않습니다. returnId: " + returnId));

        // returnState 가 " 완료" 상태인 경우 환불 테이블에 state = 0으로 추가
        if(returnState == ReturnState.COLLECT_COMPLETE.label){
            Refund refund = new Refund(returnId, false); // 초기 refundState는 false로 설정
            refundRepository.save(refund);
        }
        ReturnState state = ReturnState.fromLabel(returnState);

        returnEntity.updateReturnState(state);
    }

    // 신상품 등록
    @Transactional
    public void saveProductList(List<ProductListDto> productList) {
        for (ProductListDto productitem : productList) {
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
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category != null) {
            return category.getCategoryId();
        }
        return null;
    }

    @Transactional
    public Product saveProduct(ProductListDto productitem, Long categoryId) {
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
        for (String imgAddress : imgAddresses) {
            ProductImgs productImgs = ProductImgs.builder()
                    .productId(productId)
                    .imgAddress(imgAddress)
                    .build();

            productImgsRepository.save(productImgs);
        }
    }

    // 문의 목록 조회(카테고리별)
    @Transactional
    public Page<InquiryListResDto> getAllInquiriesByType(InquiryType inquiryType, Pageable pageable) {
        return inquiryRepository.findByInquiryType(inquiryType, pageable);
    }

    // 문의 삭제
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

    // 문의 답변 등록
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

    @Transactional
    public void updateOrderDetailState(Long detailId, String orderStatus) {
        OrderDetailState orderDetailState = null;
        switch (orderStatus) {
            case "결제 완료":
                orderDetailState = OrderDetailState.ORDER_COMPLETE;
                break;
            case "배송 준비":
                orderDetailState = OrderDetailState.DELIVERY_READY;
                break;
            case "배송중":
                orderDetailState = OrderDetailState.DELIVERY_ING;
                break;
            case "배송 완료":
                orderDetailState = OrderDetailState.DELIVERY_COMPLETE;
                break;
            case "주문 취소":
                orderDetailState = OrderDetailState.ORDER_CANCEL;
                break;
            case "환불/반품신청":
                orderDetailState = OrderDetailState.RETURN_REQUEST;
                break;
            case "환불/반품완료":
                orderDetailState = OrderDetailState.RETURN_COMPLETE;
                break;
            default:
                System.out.println("잘못된 주문 상태입니다.");
        }
        orderDetailRepository.updateOrderDetailState(detailId, orderDetailState);

    }

    // 옵션 삭제
    @Transactional
    public void deleteOptionByOptionId(Long optionId) {
        productOptionRepository.deleteByOptionId(optionId);
    }

    // 상품 목록 조회
    public List<ProductListAdminResDto> getProductListByCateogryId(Long categoryId) {
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

    // 관리자 전체 조회
    public TotalAdminResDto getAdminUserDetails() {
        List<User> allManagers = userRepository.findAllByUserRoleIsNot(Role.ROLE_USER);
        List<ManagerInfoDto> managerInfoDTOList = new ArrayList<>();

        int gmCount = adminCount(allManagers, Role.ROLE_GM);
        int smCount = adminCount(allManagers, Role.ROLE_SM);
        int pmCount = adminCount(allManagers, Role.ROLE_PM);

        for (User manager : allManagers) {
            ManagerInfoDto managerInfoDto = new ManagerInfoDto(
                    manager.getUserId(),
                    manager.getUserName(),
                    manager.getUserEmail(),
                    manager.getUserRole(),
                    manager.getUserJoinDate().atStartOfDay());
            managerInfoDTOList.add(managerInfoDto);
        }

        return new TotalAdminResDto(allManagers.size(), gmCount, smCount, pmCount, managerInfoDTOList);
    }

    // 관리자 권한 별 관리자 수 집계
    private int adminCount(List<User> users, Role role) {
        return (int) users.stream().filter(user -> user.getUserRole() == role).count();
    }

    // 권한 변경
    @Transactional
    public void changeRole(Long userId, ChangeRoleReqDto newRole){
        User user = userRepository.findByUserId(userId);

        // String -> Enum 으로 형변환
        Role role = Role.valueOf(newRole.getNewRole().toUpperCase());

        user.changeRole(role);

        userRepository.save(user); // 변경된 역할을 저장
    }


    // 사용자 검색
    @Transactional
    public SearchUserResDto searchUser(Integer dktNum){
        User userdata = userRepository.findByUserDktNum(dktNum);

        SearchUserResDto searchUserResDto = userdata.toUserInfoRes();

        return searchUserResDto ;
    }

    // 마일리지 환불
    @Transactional
    public void putRefund(RefundReqDto refundReqDto) {
        Integer percent = refundReqDto.getRefundPercent();
        Long returnId = refundReqDto.getReturnId();
        Integer price = orderDetailRepository.getOrderDetailSalePriceFindByReturnId(returnId);
        Integer amount = (int) (price * percent /100);
        orderDetailRepository.updateReturnCompleteByReturnId(returnId, OrderDetailState.RETURN_COMPLETE);
        refundRepository.updateRefundCompleteByReturnId(returnId);
        userRepository.updateUserMileageByReturnId(returnId, amount);
    }

    // 취소 목록 조회
    @Transactional
    public List<OrderCancelResDto> orderCancle(){
        return orderDetailRepository.findOrderCancelResDtosByOrderDetailState(OrderDetailState.ORDER_CANCEL);
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

    public List<OrderListAdminResDto> getOrdersByStatus(String status) {
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
                    .map(orderDetail -> {
                        Order order = orderRepository.findByOrderId(orderDetail.getOrderId());
                        if (order == null) {
                            throw new RuntimeException("Order not found for OrderDetailId: " + orderDetail.getOrderDetailId());
                        }
                        return order;
                    })
                    .collect(Collectors.toList());


            return mapOrderDetailsToDto(orderDetails, products, productOptions, productImgs, orders);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 주문 상태: " + status);
        } catch (Exception e) {
            throw new RuntimeException("주문 목록 조회 중 오류 발생", e);
        }
    }




    private List<OrderListAdminResDto> mapOrderDetailsToDto(List<OrderDetail> orderDetails,
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


                        return new OrderListAdminResDto(
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
                        return new OrderListAdminResDto();
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
    // --- 배송 목록 조회 ---

}
