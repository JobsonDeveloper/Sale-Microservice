package br.com.sales.micro.dto.swagger;

public record DefaultErrorResponseDto(
        String status,
        String message
) {
}
