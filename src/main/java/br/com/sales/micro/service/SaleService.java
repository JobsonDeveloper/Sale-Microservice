package br.com.sales.micro.service;

import br.com.sales.micro.domain.Client;
import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.dto.request.ProductBarCodeListDto;
import br.com.sales.micro.events.dto.SaleStartedEventDto;
import br.com.sales.micro.dto.response.ClientDto;
import br.com.sales.micro.dto.response.ProductDto;
import br.com.sales.micro.events.producer.SaleIventProducer;
import br.com.sales.micro.exception.*;
import br.com.sales.micro.exception.client.ClientDataIncompatibleException;
import br.com.sales.micro.exception.client.ClientNotFoundException;
import br.com.sales.micro.exception.client.ErrorRetrievingClientDataException;
import br.com.sales.micro.exception.product.ErrorRetrievingProductDataException;
import br.com.sales.micro.exception.product.ProductDataIncompatibleException;
import br.com.sales.micro.exception.product.ProductNotFoundException;
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
    private final ClientClient clientClient;
    private final SaleIventProducer saleIventProducer;

    public SaleService(
            ISaleRepository saleRespository,
            ProductClient productClient,
            ClientClient clientClient, SaleIventProducer saleIventProducer
    ) {
        this.saleRespository = saleRespository;
        this.productClient = productClient;
        this.clientClient = clientClient;
        this.saleIventProducer = saleIventProducer;
    }

    @Override
    public ProductDto getProductsData(List<Long> barCodes) {
        ProductBarCodeListDto request = new ProductBarCodeListDto(barCodes);

        try {
            return productClient.getProductsData(request);
        } catch (RetryableException e) {
            throw new ServiceUnavailableException("Product Microservice");
        } catch (FeignException e) {
            switch (e.status()) {
                case 404:
                    throw new ProductNotFoundException();
                case 400:
                    throw new ProductDataIncompatibleException();
                default:
                    throw new ErrorRetrievingProductDataException();
            }
        }
    }

    @Override
    public ClientDto getClientData(String id) {
        try {
            return clientClient.getClientData(id);
        } catch (RetryableException e) {
            throw new ServiceUnavailableException("Client Microservice");
        } catch (FeignException e) {
            switch (e.status()) {
                case 404:
                    throw new ClientNotFoundException();
                case 400:
                    throw new ClientDataIncompatibleException();
                default:
                    throw new ErrorRetrievingClientDataException();
            }
        }
    }

    @Override
    public Sale makeSale(String clientId, Long clientCpf, Double totalValue, List<Item> products) {
        Client client = Client.builder()
                .id(clientId)
                .cpf(clientCpf)
                .build();

        Sale sale = Sale.builder()
                .client(client)
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

        SaleStartedEventDto event = new SaleStartedEventDto(
                newSale.getId(),
                newSale.getClient().getId(),
                newSale.getStatus(),
                newSale.getItems()
        );

        saleIventProducer.saleStartedEvent(event);

        return newSale;
    }
}
