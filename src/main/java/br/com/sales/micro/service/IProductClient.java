package br.com.sales.micro.service;

import br.com.sales.micro.dto.request.ProductBarCodeListDto;
import br.com.sales.micro.dto.response.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "product-microservice",
        url = "${product.micro.url}"
)
public interface IProductClient {

    @PostMapping("/api/product/data")
    ProductDto getProductsData(
            @RequestBody ProductBarCodeListDto dto
    );
}
