package br.com.sales.micro.controller;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.dto.request.MakeSaleDto;
import br.com.sales.micro.dto.request.ProductBasicInfoDto;
import br.com.sales.micro.dto.response.ReturnSaleDto;
import br.com.sales.micro.dto.response.SaleInfoDto;
import br.com.sales.micro.exception.InconsistentValueException;
import br.com.sales.micro.exception.product.InsufficientProductsException;
import br.com.sales.micro.service.ISaleService;
import br.com.sales.micro.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Sale", description = "Sale operations")
public class SaleController {
    private final ISaleService iSaleService;

    public SaleController(ISaleService iSaleService) {
        this.iSaleService = iSaleService;
    }

    @PostMapping("/api/sale")
    @Operation(
            summary = "Create a sale",
            description = "Create a new sale",
            tags = {"Sale"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Sale started successfully!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnSaleDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Incompatible data!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"error\": \"Validation failed\", \"errors\": \"[...]\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or Client not found!",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Product not found!",
                                                    value = "{ \"status\": \"NOT_FOUND\", \"message\": \"Product not found!\" }"
                                            ),
                                            @ExampleObject(
                                                    name = "Client not found!",
                                                    value = "{ \"status\": \"NOT_FOUND\", \"message\": \"Client not found!\" }"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "The amount to be paid is inconsistent!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"CONFLICT\", \"message\": \"The amount to be paid is inconsistent!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error creating the sale!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"Error creating the sale!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "502",
                            description = "Product or Client microservice are unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "Product microservice unavailable!",
                                                    value = "{ \"status\": \"BAD_GATEWAY\", \"message\": \"Service 'Product Microservice' is unavailable!\" }"
                                            ),
                                            @ExampleObject(
                                                    name = "Client microservice unavailable",
                                                    value = "{ \"status\": \"BAD_GATEWAY\", \"message\": \"Service 'Client Microservice' is unavailable!\" }"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<ReturnSaleDto> makeSale(@Valid @RequestBody MakeSaleDto productBarCodeListDto) {
        String clientId = productBarCodeListDto.clientId();
        List<ProductBasicInfoDto> products = productBarCodeListDto.products();
        List<Long> barCodes = new ArrayList<>();
        double totalValue = 0.0;

        Long clientCpf = iSaleService.getClientData(clientId).client().getCpf();

        products.stream().forEach((info) -> {
            barCodes.add(info.productBarCode());
        });

        List<Item> productsData = iSaleService.getProductsData(barCodes).products();

        for (var storeProduct : productsData) {
            for (var buyProduct : products) {
                Long storedProductCode = storeProduct.getBarCode();
                Long storedProductQuantity = storeProduct.getQuantity();
                Long desiredProductCode = buyProduct.productBarCode();
                Long desiredProductQuantity = buyProduct.productQuantity();

                if (storedProductCode.equals(desiredProductCode)) {
                    if ((storedProductQuantity - desiredProductQuantity) < 0) {
                        throw new InsufficientProductsException();
                    }

                    totalValue += storeProduct.getValue() * desiredProductQuantity;
                    storeProduct.setQuantity(desiredProductQuantity);
                }
            }
        }

        if (!productBarCodeListDto.totalValue().equals(totalValue)) {
            throw new InconsistentValueException();
        }

        Sale newSale = iSaleService.makeSale(clientId, clientCpf, totalValue, productsData);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReturnSaleDto("Sale started successfully!", newSale));
    }

    @GetMapping("/api/sale/{id}/info")
    @Operation(
            summary = "Get sale info",
            description = "Return information of a sale",
            tags = {"Sale"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sale info returned successfully!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SaleInfoDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Sale not found!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"NOT_FOUND\", \"message\": \"Sale not found!\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<SaleInfoDto> getSaleInfo(
            @Parameter(description = "Sale id", required = true)
            @PathVariable String id
    ) {
        Sale sale = iSaleService.getSaleInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(new SaleInfoDto("Sale info returned successfully!", sale));
    }
}
