package br.com.sales.micro.dto.swagger.validation.fields;

import br.com.sales.micro.dto.swagger.validation.fields.FieldErrorDetailsDto;

import java.util.List;

public record FieldsErrorDto(
        String error,
        List<FieldErrorDetailsDto> errors
) {
}
