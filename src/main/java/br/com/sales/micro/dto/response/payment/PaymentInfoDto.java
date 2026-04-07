package br.com.sales.micro.dto.response.payment;

import br.com.sales.micro.domain.Status;

public record PaymentInfoDto(
        PaymentDatesDto payment,
        Status status
) {
}
