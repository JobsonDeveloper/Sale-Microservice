package br.com.sales.micro.dto.response;

import org.springframework.http.HttpStatus;

public record OperationHttpStatusCodeDto(
        String message,
        HttpStatus statusCode
) {
}
