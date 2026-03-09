package br.com.sales.micro.events.dto;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Status;

import java.util.List;

public record SaleStartedEventDto(
        String id,
        String clientId,
        Status status,
        List<Item> items
) {
}
