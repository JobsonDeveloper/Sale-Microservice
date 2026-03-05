package br.com.sales.micro.dto.response;

import br.com.sales.micro.domain.Item;

import java.util.List;

public record ProductDto(
        String message,
        List<Item> products
) {
}
