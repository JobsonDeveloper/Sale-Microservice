package br.com.sales.micro.controller;

import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.dto.request.MakeSaleDto;
import br.com.sales.micro.dto.request.ProductBasicInfoDto;
import br.com.sales.micro.dto.response.ProductDto;
import br.com.sales.micro.exception.InconsistentValueException;
import br.com.sales.micro.service.SaleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Sale", description = "Sale operations")
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/api/sale")
    public ResponseEntity<Sale> makeSale(@Valid @RequestBody MakeSaleDto productBarCodeListDto) {
        String clientId = productBarCodeListDto.clientId();
        List<ProductBasicInfoDto> products = productBarCodeListDto.products();
        List<Long> barCodes = new ArrayList<>();
        double totalValue = 0.0;

        products.stream().forEach((info) -> {
            barCodes.add(info.productBarCode());
        });

        ProductDto data = saleService.getProductsData(barCodes);

        for (var storeProduct : data.products()) {
            for (var buyProduct : products) {
                if (storeProduct.getBarCode().equals(buyProduct.productBarCode())) {
                    totalValue += storeProduct.getValue() * buyProduct.productQuantity();;
                    storeProduct.setQuantity(buyProduct.productQuantity());
                }
            }
        }

        if(!productBarCodeListDto.totalValue().equals(totalValue)) {
            throw new InconsistentValueException();
        }

        Sale newSale = saleService.makeSale(clientId, totalValue, data.products());
        return ResponseEntity.status(HttpStatus.CREATED).body(newSale);
    }
}
