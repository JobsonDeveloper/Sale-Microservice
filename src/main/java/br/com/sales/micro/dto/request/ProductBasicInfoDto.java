package br.com.sales.micro.dto.request;

public record ProductBasicInfoDto(
        Long productBarCode,
        Integer productQuantity
) {
}
