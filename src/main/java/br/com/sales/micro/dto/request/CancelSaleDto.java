package br.com.sales.micro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelSaleDto(
        @NotNull(message = "The user id is required!") @Size(min = 1, message = "The user id must be valid!") String userId,
        @NotNull(message = "The sale id is required!") @Size(min = 1, message = "The sale id must be valid!") String saleId
) {
}
