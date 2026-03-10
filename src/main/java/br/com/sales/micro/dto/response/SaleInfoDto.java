package br.com.sales.micro.dto.response;

import br.com.sales.micro.domain.Sale;

public record SaleInfoDto(
        String message,
        Sale sale
) {
}
