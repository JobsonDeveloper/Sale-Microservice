package br.com.sales.micro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaleCompletedDto(
        @NotNull(message = "The sale id is required!") @Size(min = 1, message = "The sale id must be valid!") String saleId
) {
}
