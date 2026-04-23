package br.com.sales.micro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MakeSaleDto(
        @NotNull(message = "User id is required!") String userId,
        @Size(min = 1, message = "The product list must be valid!") List<ProductBasicInfoDto> products,
        @NotNull(message = "The total value is required!") Double totalValue
) {
}
