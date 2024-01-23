package com.dmarket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResDto {
    private Long wishCount;

    private List<WishlistItemDto> wishListItem;

}
