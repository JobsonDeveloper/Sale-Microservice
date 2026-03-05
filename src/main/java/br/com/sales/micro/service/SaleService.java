package br.com.sales.micro.service;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.dto.request.ProductBarCodeListDto;
import br.com.sales.micro.dto.response.ProductDto;
import br.com.sales.micro.exception.*;
import br.com.sales.micro.respository.ISaleRepository;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService implements ISaleService {
    private final ISaleRepository saleRespository;

    private final ProductClient productClient;

    public SaleService(ISaleRepository saleRespository, ProductClient productClient) {
        this.saleRespository = saleRespository;
        this.productClient = productClient;
    }

    public ProductDto getProductsData(List<Long> barCodes) {
        ProductBarCodeListDto request = new ProductBarCodeListDto(barCodes);

        try {
            return productClient.getProductsData(request);
        } catch (RetryableException e) {
            throw new ServiceUnavailableException("Product Microservice");
        }
        catch (FeignException e) {
            switch (e.status()) {
                case 404: throw new ProductNotFoundException();
                case 400: throw new IncompatibleRequestDataException();
                default: throw new ErrorRetrievingProductDataException();
            }
        }
    }

    @Override
    public Sale makeSale(String clientId, Double totalValue, List<Item> products) {
        Sale sale = Sale.builder()
                .clientId(clientId)
                .status(Status.CREATED)
                .date(LocalDateTime.now())
                .totalValue(totalValue)
                .items(products)
                .created_at(LocalDateTime.now())
                .build();

        Sale newSale = saleRespository.save(sale);

        if (newSale.getId() == null) {
            throw new ErrorCreatingTheSaleException();
        }

        return newSale;
    }
}
