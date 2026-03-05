package br.com.sales.micro.dto.request;

public record ProductBasicInfoDto(
        Long productBarCode,
        Long productQuantity
) {
}
