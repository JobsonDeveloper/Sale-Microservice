package br.com.sales.micro.event.dto;

import br.com.sales.micro.domain.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryEventDto(
        String saleId,
        Status status
) {
}