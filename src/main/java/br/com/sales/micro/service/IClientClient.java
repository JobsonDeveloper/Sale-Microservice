package br.com.sales.micro.service;

import br.com.sales.micro.dto.response.ClientDto;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "client-microservice",
        url = "${client.micro.url}"
)
public interface IClientClient {

    @GetMapping("/api/client/{id}/info")
    ClientDto getClientData(
            @Parameter(description = "Client id", required = true)
            @PathVariable String id
    );
}
