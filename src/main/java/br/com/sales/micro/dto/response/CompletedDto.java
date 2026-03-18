package br.com.sales.micro.dto.response;

import br.com.sales.micro.domain.Completed;

public record CompletedDto(
        String message,
        Completed data
) {
}
