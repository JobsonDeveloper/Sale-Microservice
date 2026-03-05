package br.com.sales.micro.dto.request;

import java.util.List;

public record ProductBarCodeListDto(
        List<Long> products
) {
}
