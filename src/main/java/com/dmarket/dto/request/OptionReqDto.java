package com.dmarket.dto.request;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionReqDto {
    private String optionName;

    private String optionValue;

    @NotNull
    private Integer optionQuantity;
}
