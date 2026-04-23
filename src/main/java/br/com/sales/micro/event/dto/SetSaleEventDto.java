package br.com.sales.micro.event.dto;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Status;

import java.util.List;

public record SetSaleEventDto(
        String id,
        String userId,
        Status status,
        List<Item> items
) {
}
