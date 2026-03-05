package br.com.sales.micro.events.dto;

import br.com.sales.micro.domain.Item;

import java.util.List;

public record SaleStartedEventDto(
        String id,
        String clientId,
        List<Item> items
) {
}
