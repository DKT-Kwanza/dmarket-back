package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.dmarket.dto.common.WishlistItemDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResDto {
    private long wishCount;
    private List<WishlistItemDto> wishListItem;

    public int getWishCount() {
        return wishListItem.size();
    }
}
