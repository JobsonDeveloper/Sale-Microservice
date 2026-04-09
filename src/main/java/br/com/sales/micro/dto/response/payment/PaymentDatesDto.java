package br.com.sales.micro.dto.response.payment;

import java.time.Instant;

public record PaymentDatesDto(
    Instant dateApproved
) {
}
