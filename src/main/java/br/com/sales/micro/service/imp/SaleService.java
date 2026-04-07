package br.com.sales.micro.service.imp;

import br.com.sales.micro.domain.*;
import br.com.sales.micro.dto.request.ProductBarCodeListDto;
import br.com.sales.micro.dto.response.payment.PaymentDto;
import br.com.sales.micro.event.dto.SetSaleEventDto;
import br.com.sales.micro.dto.response.ClientDto;
import br.com.sales.micro.dto.response.ProductDto;
import br.com.sales.micro.event.producer.SaleEventProducer;
import br.com.sales.micro.exception.*;
import br.com.sales.micro.exception.client.ClientDataIncompatibleException;
import br.com.sales.micro.exception.client.ClientNotFoundException;
import br.com.sales.micro.exception.client.ErrorRetrievingClientDataException;
import br.com.sales.micro.exception.product.ErrorRetrievingProductDataException;
import br.com.sales.micro.exception.product.ProductDataIncompatibleException;
import br.com.sales.micro.exception.product.ProductNotFoundException;
import br.com.sales.micro.respository.ICanceledRepository;
import br.com.sales.micro.respository.ISaleRepository;
import br.com.sales.micro.service.IClientClient;
import br.com.sales.micro.service.IPaymentClient;
import br.com.sales.micro.service.ISaleService;
import br.com.sales.micro.service.IProductClient;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService implements ISaleService {
    private final ISaleRepository iSaleRepository;
    private final IProductClient IProductClient;
    private final IClientClient IClientClient;
    private final SaleEventProducer saleEventProducer;
    private final ICanceledRepository iCanceledRepository;
    private final IPaymentClient iPaymentClient;

    public SaleService(
            ISaleRepository iSaleRepository,
            IProductClient IProductClient,
            IClientClient IClientClient,
            SaleEventProducer saleEventProducer,
            ICanceledRepository iCanceledRepository,
            IPaymentClient iPaymentClient
    ) {
        this.iSaleRepository = iSaleRepository;
        this.IProductClient = IProductClient;
        this.IClientClient = IClientClient;
        this.saleEventProducer = saleEventProducer;
        this.iCanceledRepository = iCanceledRepository;
        this.iPaymentClient = iPaymentClient;
    }

    @Override
    public ProductDto getProductsData(List<Long> barCodes) {
        ProductBarCodeListDto request = new ProductBarCodeListDto(barCodes);

        try {
            return IProductClient.getProductsData(request);
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
            return IClientClient.getClientData(id);
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
    public Sale makeSale(
            String clientId,
            String clientCpf,
            Double totalValue,
            List<Item> products
    ) {
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

        Sale newSale = iSaleRepository.save(sale);

        if (newSale.getId() == null) {
            throw new ErrorCreatingTheSaleException();
        }

        SetSaleEventDto event = new SetSaleEventDto(
                newSale.getId(),
                newSale.getClient().getId(),
                newSale.getStatus(),
                newSale.getItems()
        );

        saleEventProducer.setSaleEvent(event);

        return newSale;
    }

    @Override
    public Sale getSaleInfo(String id) {
        Optional<Sale> sale = iSaleRepository.findById(id);

        if (!sale.isPresent()) {
            throw new SaleNotFoundException();
        }

        return sale.get();
    }

    @Override
    public String cancelSale(String saleId, String clientId) {
        Sale sale = iSaleRepository.findById(saleId).orElseThrow(SaleNotFoundException::new);
        Status saleStatus = sale.getStatus();
        String saleClientId = sale.getClient().getId();
        String response = "Sale canceled successfully!";

        if (!saleClientId.equals(clientId)) throw new PermissionDeniedException();

        if (saleStatus.equals(Status.DELIVERED))
            throw new PermissionDeniedException("It is no longer possible to cancel the sale!");

        if (saleStatus.equals(Status.CANCELED)) throw new SaleAlreadyCancelledException();

        if (saleStatus.equals(Status.CREATED) || saleStatus.equals(Status.PENDING_PAYMENT)) {
            iSaleRepository.deleteById(saleId);

            Canceled canceled = Canceled.builder()
                    .status(Status.CANCELED)
                    .date(sale.getDate())
                    .totalValue(sale.getTotalValue())
                    .client(sale.getClient())
                    .items(sale.getItems())
                    .created_at(LocalDateTime.now())
                    .build();

            iCanceledRepository.save(canceled);
        } else { // PAID, SHIPPEND
            PaymentDto payment = iPaymentClient.getPaymentInfo(saleId);
            Instant dateApproved = payment.payment().payment().dateApproved();
            boolean mustOfTwoHoursOld = Duration
                    .between(dateApproved, Instant.now())
                    .abs()
                    .compareTo(Duration.ofHours(2)) >= 0;

            if (mustOfTwoHoursOld) throw new PermissionDeniedException("Cancellation is no longer possible!");
            response = "The sale cancellation is being processed!";
        }

        saleEventProducer.setSaleEvent(new SetSaleEventDto(
                saleId,
                clientId,
                Status.CANCELED,
                sale.getItems()
        ));

        return response;
    }
}
