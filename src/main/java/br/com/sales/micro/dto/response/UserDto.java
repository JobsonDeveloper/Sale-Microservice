package br.com.sales.micro.dto.response;

import br.com.sales.micro.domain.User;

public record UserDto(
        String message,
        User user
) {
}
