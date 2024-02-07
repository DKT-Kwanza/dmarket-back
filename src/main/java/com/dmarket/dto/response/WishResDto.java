package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.dmarket.dto.common.WishlistItemDto;
import org.springframework.data.domain.Page;

public class WishResDto{

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WishlistResDto {
        private long wishCount;
        private Page<WishlistItemDto> wishListItem;

        public void setWishCount(Long aLong) {
            this.wishCount = aLong;

        }

        public void setWishListItem(Page<WishlistItemDto> content) {
            this.wishListItem = content;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class IsWishResDto {
        private Boolean isWish;
    }
}
