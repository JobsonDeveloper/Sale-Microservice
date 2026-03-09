package br.com.sales.micro.dto.response;

import br.com.sales.micro.domain.Client;

public record ClientDto(
        String message,
        Client client
) {
}
