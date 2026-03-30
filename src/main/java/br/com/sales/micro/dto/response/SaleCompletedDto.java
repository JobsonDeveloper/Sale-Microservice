package br.com.sales.micro.dto.response;

import br.com.sales.micro.domain.Completed;

public record SaleCompletedDto(
        String message,
        Completed data
) {
}
