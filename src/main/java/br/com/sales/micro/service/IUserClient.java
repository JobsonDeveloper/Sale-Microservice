package br.com.sales.micro.service;

import br.com.sales.micro.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-microservice",
        url = "${user.micro.url}"
)
public interface IUserClient {

    @GetMapping("/api/user/{id}/info")
    UserDto getUserData(
            @Parameter(description = "User id", required = true)
            @PathVariable String id
    );
}
