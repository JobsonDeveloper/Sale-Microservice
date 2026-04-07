package br.com.sales.micro.service;

import br.com.sales.micro.dto.response.payment.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "payment-microservice",
        url = "${payment.micro.url}"
)
public interface IPaymentClient {

    @GetMapping("/api/payment/saleId/info")
    PaymentDto getPaymentInfo(@PathVariable(name = "saleId", required = true) String saleId);
}
