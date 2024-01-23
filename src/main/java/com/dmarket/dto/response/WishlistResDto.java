package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResDto {
    private int wishCount;
    private List<WishlistItemDto> wishListItem;
    public int getWishCount() {
        return wishListItem.size();
    }
}

