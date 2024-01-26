package com.dmarket.service;

import com.dmarket.domain.product.Product;
import com.dmarket.domain.user.User;
import com.dmarket.dto.request.ProductToOrderReqDto;
import com.dmarket.dto.response.ProductToOrderRespDto;
import com.dmarket.repository.product.ProductImgsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    //조회가 아닌 메서드들은 꼭 @Transactional 넣어주세요 (CUD, 입력/수정/삭제)

    private final UserService userService;
    private final ProductService productService;
    private final ProductImgsRepository productImgsRepository;

    public ProductToOrderRespDto getProductToOrder(ProductToOrderReqDto dto) {

        Integer totalPrice = 0;
        Integer totalPay = 0;
        ProductToOrderRespDto respDto = new ProductToOrderRespDto();
        ArrayList<ProductToOrderRespDto.ProductToOrder> productList = new ArrayList<>();
        List<ProductToOrderReqDto.ProductToOrder> products = dto.getProductList();

        for (ProductToOrderReqDto.ProductToOrder productToOrder : products) {

            Long productId = productToOrder.getProductId();
            Integer productCount = productToOrder.getProductCount();
            Long optionId = productToOrder.getOptionId();
            Product product = productService.findByProductId(productId);

            ProductToOrderRespDto.ProductToOrder productDetail = new ProductToOrderRespDto.ProductToOrder();
            productDetail.setProductId(productId);
            productDetail.setProductBrand(product.getProductBrand());
            productDetail.setProductName(product.getProductName());
            productDetail.setOptionId(optionId);
            productDetail.setProductOption(productService.findOptionByOptionId(optionId).getOptionValue());
            productDetail.setProductCount(productCount);
            productDetail.setProductImg(productImgsRepository.findFirstByProductId(productId).getImgAddress());
            productDetail.setProductTotalPrice(product.getProductPrice()*productCount);
            productDetail.setProductTotalSalePrice(product.getProductSalePrice()*productCount);

            totalPrice += product.getProductPrice() * productCount;
            totalPay += product.getProductSalePrice() * productCount;
            productList.add(productDetail);
        }

        User user = userService.findById(dto.getUserId());
        respDto.setUserName(user.getUserName());
        respDto.setUserPhoneNum(user.getUserPhoneNum());
        respDto.setUserEmail(user.getUserEmail());
        respDto.setUserPostalCode(user.getUserPostalCode());
        respDto.setUserAddress(user.getUserAddress());
        respDto.setUserDetailAddress(user.getUserAddressDetail());
        respDto.setTotalPrice(totalPrice);
        respDto.setTotalPay(totalPay);
        respDto.setProductList(productList);

        return respDto;
    }
}
