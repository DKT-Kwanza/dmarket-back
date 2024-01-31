package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.dmarket.dto.common.WishlistItemDto;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResDto {
    private long wishCount;
    private Page<WishlistItemDto> wishListItem;

    public void setWishCount(Long aLong) {
        this.wishCount = aLong;

    }

    public void setWishListItem(Page<WishlistItemDto> content) {
        this.wishListItem = content;
    }
}
