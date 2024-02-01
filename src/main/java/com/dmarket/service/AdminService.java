package com.dmarket.service;

import com.dmarket.constant.*;
import com.dmarket.domain.board.Faq;
import com.dmarket.domain.board.Inquiry;
import com.dmarket.domain.board.InquiryReply;
import com.dmarket.domain.board.Notice;
import com.dmarket.domain.order.Refund;
import com.dmarket.domain.order.Return;
import com.dmarket.domain.product.*;
import com.dmarket.domain.user.Mileage;
import com.dmarket.domain.user.MileageReq;
import com.dmarket.domain.user.User;
import com.dmarket.dto.common.*;
import com.dmarket.dto.request.OptionReqDto;
import com.dmarket.dto.request.ProductReqDto;
import com.dmarket.dto.request.RefundReqDto;
import com.dmarket.dto.request.UserReqDto;
import com.dmarket.dto.response.*;
import com.dmarket.exception.BadRequestException;
import com.dmarket.exception.ConflictException;
import com.dmarket.exception.NotFoundException;
import com.dmarket.jwt.JWTUtil;
import com.dmarket.notification.sendNotificationEvent;
import com.dmarket.repository.board.FaqRepository;
import com.dmarket.repository.board.InquiryReplyRepository;
import com.dmarket.repository.board.InquiryRepository;
import com.dmarket.repository.board.NoticeRepository;
import com.dmarket.repository.order.OrderDetailRepository;
import com.dmarket.repository.order.OrderRepository;
import com.dmarket.repository.order.RefundRepository;
import com.dmarket.repository.order.ReturnRepository;
import com.dmarket.repository.product.*;
import com.dmarket.repository.user.MileageRepository;
import com.dmarket.repository.user.MileageReqRepository;
import com.dmarket.repository.user.UserRepository;
import com.dmarket.repository.user.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dmarket.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    // 조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final MileageRepository mileageRepository;
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
    private final JWTUtil jwtUtil;
    private final ApplicationEventPublisher publisher;



    private static final int PAGE_POST_COUNT = 10;

    @Transactional
    public void deleteUserByUserId(Long userId) {
        // 존재하지 않는 사용자의 경우 에러
        boolean isUser = userRepository.existsById(userId);
        if (!isUser) {
            throw new NotFoundException(USER_NOT_FOUND);
        }
        userRepository.deleteByUserId(userId);
    }

    public List<UserResDto.Search> getUsersFindByDktNum(Integer userDktNum) {
        List<User> users = userRepository.getUsersFindByUserDktNum(userDktNum);
        return users.stream()
                .map(UserResDto.Search::new)
                .collect(Collectors.toList());
    }

    public Page<NoticeResDto> getNotices(int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);
        return noticeRepository.getNotices(pageable);
    }

    @Transactional
    public Page<NoticeResDto> postNotice(Long userId, String noticeTitle, String noticeContents, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);

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
    public Page<MileageCommonDto.MileageReqListDto> getMileageRequests(String status, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "mileageReqDate"));
        Page<MileageCommonDto.MileageReqListDto> dtos;

        if (status.equals("PROCESSING")) {
            dtos = mileageReqRepository.findAllByProcessing(pageable);
        } else if (status.equals("PROCESSED")) {
            dtos = mileageReqRepository.findAllByProcessed(pageable);
        } else {
            throw new BadRequestException(INVALID_STATE_PARAM);
        }
        return dtos;
    }

    // 마일리지 충전 요청 처리
    @Transactional
    public void approveMileageReq(Long mileageReqId, boolean request) {
        MileageReq mileageReq = findMileageReqById(mileageReqId);
        User user = findUserById(mileageReq.getUserId());
        if (request) {
            mileageReq.updateState(MileageReqState.APPROVAL);
            user.updateMileage(mileageReq.getMileageReqAmount());
            publisher.publishEvent(sendNotificationEvent.of("mileage", user.getUserId(),
                    user.getUserName() + "님의 " + mileageReq.getMileageReqAmount() + "마일리지 충전 요청이 승인되었습니다.",
                    "/api/users/" + user.getUserId() + "/mypage/mileage-history"));
        } else {
            mileageReq.updateState(MileageReqState.REFUSAL);
        }
    }

    @Transactional
    public void deleteNoticeByNoticeId(Long noticeId) {
        noticeRepository.deleteByNoticeId(noticeId);
    }

    // 사용자 ID로 찾기
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    // 사용자 마일리지 요청 찾기
    public MileageReq findMileageReqById(Long mileageReqId) {
        return mileageReqRepository.findById(mileageReqId)
                .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND));
    }

    // FAQ 조회
    public Page<Faq> getAllFaqs(FaqType faqType, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);
        return faqRepository.findFaqType(faqType, pageable);
    }

    public Page<FaqResDto.FaqListResDto> mapToFaqListResDto(Page<Faq> faqsPage) {
        return faqsPage.map(faq -> new FaqResDto.FaqListResDto(
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
        Category category = categoryRepository.findIdByCategoryName(productReqDto.getCategoryName());

        Long categoryId = category.getCategoryId();

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
    public ProductResDto.ProductInfoResDto getProductInfo(Long productId) {
        // 싱품 정보 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
        // 상품의 카테고리 depth 1, depth2 조회 후 합치기
        Category category = categoryRepository.findByCategoryId(product.getCategoryId());
        String productCategory = category.getParent().getCategoryName() + " / " + category.getCategoryName();
        // 상품의 리뷰 개수 조회
        Long reviewCnt = productReviewRepository.countByProductId(productId);
        // 상품 옵션 목록, 옵션별 재고 조회
        List<ProductCommonDto.ProductOptionDto> opts = productOptionRepository.findOptionsByProductId(productId);
        // 상품 이미지 목록 조회
        List<String> imgs = productImgsRepository.findAllByProductId(productId);
        // DTO 생성 및 반환
        return new ProductResDto.ProductInfoResDto(product, productCategory, reviewCnt, opts, imgs);
    }

    public Page<AdminResDto.AdminReviewsResDto> getProductReviews(int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);
        return productReviewRepository.getProductReviews(pageable);
    }

    // 상품 QnA 조회
    public Page<QnaDto> getQnaList(int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "qnaCreatedDate"));
        return qnaRepository.findAllQna(pageable);
    }

    // 상품 QnA 상세(개별) 조회
    public QnaResDto.QnaDetailResDto getQnADetail(Long qnaId) {
        return qnaRepository.findQnaAndReply(qnaId)
                .orElseThrow(() -> new NotFoundException(QNA_NOT_FOUND));
    }

    // 상품 QnA 답변 작성
    @Transactional
    public QnaResDto.QnaDetailResDto createQnaReply(Long qnaId, String qnaReplyContents) {
        // QnA 존재 확인
        Qna qna = qnaRepository.findById(qnaId).orElseThrow(() -> new NotFoundException(QNA_NOT_FOUND));
        // 답변 있는지 확인
        if (qna.getQnaState()) {
            throw new ConflictException(ALREADY_SAVED_REPLY);
        }
        // 답변 저장
        QnaReply qnaReply = QnaReply.builder()
                .qnaId(qnaId)
                .qnaReplyContents(qnaReplyContents)
                .build();
        qnaReplyRepository.save(qnaReply);

        // QnA 답변 상태 변경 -> 답변 대기
        qna.updateState(true);

        return getQnADetail(qnaId);
    }

    // 상품 QnA 답변 삭제
    @Transactional
    public QnaResDto.QnaDetailResDto deleteQnaReply(Long qnaReplyId) {
        // QnA 번호 가져오기
        Long qnaId = qnaReplyRepository.findQnaIdByQnaReplyId(qnaReplyId);

        // 답변 존재하지 않으면 에러
        if (qnaId == null) {
            throw new NotFoundException(REPLY_NOT_FOUND);
        }

        // QnA 존재 확인
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new NotFoundException(QNA_NOT_FOUND));

        // QnA 답변 삭제
        qnaReplyRepository.deleteById(qnaReplyId);

        // 답변 상태 변경 -> 답변 대기
        qna.updateState(false);

        return getQnADetail(qnaId);
    }

    // 반품 상태 리스트
    public ReturnResDto.ReturnListResDto getReturns(String returnStatus, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);
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
                throw new NotFoundException(STATE_NOT_FOUND);
        }

        Page<ReturnDto> returnDto = returnRepository.getReturnsByReturnState(returnState, pageable);
        ReturnResDto.ReturnListResDto returnListResDto = returnRepository.getReturnsCount();
        returnListResDto.setReturnList(returnDto);
        return returnListResDto;

    }

    // 반품 상태 업데이트
    @Transactional
    public void updateReturnState(Long returnId, String returnState) {
        Return returnEntity = returnRepository.findById(returnId)
                .orElseThrow(() -> new NotFoundException(RETURN_NOT_FOUND));

        System.out.println("returnState = " + returnState);
        // returnState 가 " 완료" 상태인 경우 환불 테이블에 state = 0으로 추가
        if (Objects.equals(returnState, ReturnState.COLLECT_COMPLETE.label)) {
            Refund refund = new Refund(returnId, false); // 초기 refundState는 false로 설정
            refundRepository.save(refund);
        }
        ReturnState state = ReturnState.fromLabel(returnState);
        returnEntity.updateReturnState(state);
    }

    // 신상품 등록
    @Transactional
    public void saveProductList(ProductReqDto.ProductListDto productList) {
        // CategoryId 가져오기
        Long categoryId = getCategoryByCategoryName(productList.getCategoryName());

        // Product 저장
        Product savedProduct = saveProduct(productList, categoryId);

        // OptionList 저장
        saveProductOptions(savedProduct.getProductId(), productList.getOptionList());

        // ProductImgs 저장
        saveProductImgs(savedProduct.getProductId(), productList.getImgList());
    }

    @Transactional
    public Long getCategoryByCategoryName(String categoryName) {
        Category category = categoryRepository.findIdByCategoryName(categoryName);
        if (category != null) {
            return category.getCategoryId();
        }
        return null;
    }

    @Transactional
    public Product saveProduct(ProductReqDto.ProductListDto productitem, Long categoryId) {
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
    public void saveProductOptions(Long productId, List<ProductReqDto.ProductListDto.Option> optionList) {
        for (ProductReqDto.ProductListDto.Option option : optionList) {
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
    public Page<InquiryResDto.InquiryListResDto> getAllInquiriesByType(InquiryType inquiryType, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);
        return inquiryRepository.findByInquiryType(inquiryType, pageable);
    }

    // 문의 삭제
    @Transactional
    public void deleteInquiry(Long inquiryId) {
        Inquiry inquiryOptional = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new NotFoundException(INQUIRY_NOT_FOUND));

        // 해당 문의의 답변을 모두 삭제
        inquiryReplyRepository.deleteByInquiryId(inquiryId);

        inquiryRepository.deleteById(inquiryId);
    }

    // 문의 답변 등록
    @Transactional
    public InquiryReply createInquiryReply(InquiryReply inquiryReply) {
        // 문의 아이디와 일치하지 않으면 등록하지 않음
        Inquiry inquiry = inquiryRepository.findById(inquiryReply.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "문의 아이디와 일치하지 않음, inquiry ID: " + inquiryReply.getInquiryId()));

        // 이미 답변이 등록된 문의인지 확인
        if (inquiry.getInquiryState()) {
            throw new IllegalArgumentException("이미 답변이 등록된 문의, inquiry ID: " + inquiryReply.getInquiryId());
        }

        inquiry.updateStatus(true); // 문의 상태를 1 (답변 완료)로 변경

        InquiryReply savedInquiryReply = inquiryReplyRepository.save(inquiryReply);


        return savedInquiryReply;
    }

    // 문의 답변 등록 - response
    public InquiryCommonDto.InquiryDetailsDto getInquiryDetails(Long inquiryReplyId) {
        return inquiryRepository.findInquiryDetailsByInquiryReplyId(inquiryReplyId);
    }

    // 문의 답변 삭제
    @Transactional
    public void deleteInquiryReply(Long inquiryReplyId) {
        InquiryReply inquiryReply = inquiryReplyRepository.findById(inquiryReplyId)
                .orElseThrow(() -> new NotFoundException(REPLY_NOT_FOUND));

        // inquiry State false 로 변경
        Inquiry inquiry = inquiryRepository.findById(inquiryReply.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid inquiry ID: " + inquiryReply.getInquiryId()));
        inquiry.updateStatus(false);

        inquiryReplyRepository.deleteById(inquiryReplyId);
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
                throw new NotFoundException(STATE_NOT_FOUND);
        }
        orderDetailRepository.updateOrderDetailState(detailId, orderDetailState);
    }

    // 옵션 삭제
    @Transactional
    public void deleteOptionByOptionId(Long optionId) {
        productOptionRepository.deleteByOptionId(optionId);
    }

     //상품 목록 조회
    public Page<ProductResDto.ProductListAdminResDto> getProductListByCateogryId(Long cateId, int pageNo) {
        pageNo = pageVaildation(pageNo);
        List<Product> products = categoryRepository.findProductsByCategoryId(cateId);
        Category category = categoryRepository.findByCategoryId(cateId);

        // 제품 목록을 처리하고 DTO 목록을 만듭니다.
        List<ProductResDto.ProductListAdminResDto> result = new ArrayList<>();
        for (Product product : products) {
            Long productId = product.getProductId();
            List<ProductCommonDto.ProductOptionDto> options = productOptionRepository.findOptionsByProductId(productId);
            List<String> imgs = productImgsRepository.findAllByProductId(productId);
            result.add(new ProductResDto.ProductListAdminResDto(product, category, options, imgs));
        }
        return new PageImpl<>(result);
    }

    //상품 목록 검색 조회
    public Page<ProductResDto.ProductListAdminResDto> getProductListBySearch(Long cateId, String query, int pageNo){
        pageNo = pageVaildation(pageNo);
        List<Product> products = categoryRepository.findProductsByQuery(cateId, query);
        Category category = categoryRepository.findByCategoryId(cateId);

        List<ProductResDto.ProductListAdminResDto> result = new ArrayList<>();
        for (Product product : products) {
            Long productId = product.getProductId();
            List<ProductCommonDto.ProductOptionDto> options = productOptionRepository.findOptionsByProductId(productId);
            List<String> imgs = productImgsRepository.findAllByProductId(productId);
            result.add(new ProductResDto.ProductListAdminResDto(product, category, options, imgs));
        }
        return new PageImpl<>(result);
    }

    // 관리자 전체 조회
    public UserResDto.TotalAdminResDto getAdminUserDetails() {
        List<User> allManagers = userRepository.findAllByUserRoleIsNot(Role.ROLE_USER);
        List<AdminResDto.ManagerInfoDto> managerInfoDTOList = new ArrayList<>();

        int gmCount = adminCount(allManagers, Role.ROLE_GM);
        int smCount = adminCount(allManagers, Role.ROLE_SM);
        int pmCount = adminCount(allManagers, Role.ROLE_PM);

        for (User manager : allManagers) {
            AdminResDto.ManagerInfoDto managerInfoDto = new AdminResDto.ManagerInfoDto(
                    manager.getUserId(),
                    manager.getUserName(),
                    manager.getUserEmail(),
                    manager.getUserRole(),
                    manager.getUserJoinDate().atStartOfDay());
            managerInfoDTOList.add(managerInfoDto);
        }

        return new UserResDto.TotalAdminResDto(allManagers.size(), gmCount, smCount, pmCount, managerInfoDTOList);
    }

    // 관리자 권한 별 관리자 수 집계
    private int adminCount(List<User> users, Role role) {
        return (int) users.stream().filter(user -> user.getUserRole() == role).count();
    }

    // 권한 변경
    @Transactional
    public UserCommonDto.TokenResponseDto changeRole(Long userId, UserReqDto.ChangeRole newRole) {
        User user = userRepository.findByUserId(userId);

        // String -> Enum 으로 형변환
        Role role = Role.valueOf(newRole.getNewRole().toUpperCase());
        user.changeRole(role);
        userRepository.save(user); // 변경된 역할을 저장

        // 토큰 재발급
        String newaccessToken = jwtUtil.createAccessJwt(userId, newRole.getNewRole(), user.getUserEmail());
        String newrefreshToken = jwtUtil.createRefreshJwt();

        return new UserCommonDto.TokenResponseDto(newaccessToken, newrefreshToken, userId);
    }

    // 사용자 검색
    @Transactional
    public UserResDto.SearchUser searchUser(Integer dktNum) {
        User userdata = userRepository.findByUserDktNum(dktNum);

        UserResDto.SearchUser searchUserDto = userdata.toUserInfoRes();

        return searchUserDto;
    }

    // 마일리지 환불
    @Transactional
    public void putRefund(RefundReqDto refundReqDto) {
        Integer percent = refundReqDto.getRefundPercent();
        Long returnId = refundReqDto.getReturnId();
        Integer price = orderDetailRepository.getOrderDetailSalePriceFindByReturnId(returnId);
        Integer amount = (int) (price * percent / 100);
        orderDetailRepository.updateReturnCompleteByReturnId(returnId, OrderDetailState.RETURN_COMPLETE);
        updateReturnState(returnId, "환불 완료");
        refundRepository.updateRefundCompleteByReturnId(returnId);
        userRepository.updateUserMileageByReturnId(returnId, amount);
        User user = userRepository.getUserFindByReturnId(returnId);
        // 사용자 마일리지 사용 내역에 추가
        Mileage mileage = Mileage.builder()
                .userId(user.getUserId())
                .remainMileage(amount)
                .changeMileage(user.getUserMileage())
                .mileageInfo(MileageContents.REFUND)
                .build();
        mileageRepository.save(mileage);
    }

    // 취소 목록 조회
    @Transactional
    public List<OrderResDto.OrderCancelResDto> orderCancle() {
        return orderDetailRepository.findOrderCancelResDtosByOrderDetailState(OrderDetailState.ORDER_CANCEL)
                .stream()
                .map(row -> new OrderResDto.OrderCancelResDto(
                        (Long) row[0],
                        (Long) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        (LocalDateTime) row[7],
                        (Integer) row[8],
                        (OrderDetailState) row[9]))
                .collect(Collectors.toList());
    }

    // 상품 재고 추가
    @Transactional
    public void addProductStock(ProductReqDto.StockReqDto stockReqDto) {
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
    public ProductResDto.ProductInfoOptionResDto getProductInfoWithOption(Long productId) {
        try {
            List<ProductResDto.ProductInfoOptionResDto> productDetails = productRepository
                    .findProductDetails(productId);

            if (!productDetails.isEmpty()) {
                ProductResDto.ProductInfoOptionResDto productDetail = productDetails.get(0);

                return ProductResDto.ProductInfoOptionResDto.builder()
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
    public OrderCommonDto.OrderDetailStateCountsDto getOrderDetailStateCounts() {
        return orderRepository.getOrderDetailStateCounts();
    }

    public Page<OrderListAdminResDto> getOrdersByStatus(String status, int pageNo) {
        pageNo = pageVaildation(pageNo);
        Pageable pageable = PageRequest.of(pageNo, PAGE_POST_COUNT);
        try {
            OrderDetailState orderStatus = OrderDetailState.fromLabel(status);
            if (orderStatus == null) {
                throw new IllegalArgumentException("유효하지 않은 주문 상태: " + status);
            }
            return orderDetailRepository.findByStatus(orderStatus, pageable);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 주문 상태: " + status);
        } catch (Exception e) {
            throw new RuntimeException("주문 목록 조회 중 오류 발생", e);
        }
    }

    // 페이지 번호 유효성 검사 메소드
    public int pageVaildation(int page) {
        page = page > 0 ? page - 1 : page;
        return page;
    }

    // 문의 내역 상세 조회
    @Transactional
    public InquiryResDto.InquiryDetailResDto getInquiryDetail(Long inquiryId) {
        return inquiryRepository.findInquiryDetailById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 ID: " + inquiryId));
    }

}
