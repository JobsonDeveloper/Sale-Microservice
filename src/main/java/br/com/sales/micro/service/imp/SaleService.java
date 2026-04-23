package br.com.sales.micro.service.imp;

import br.com.sales.micro.domain.*;
import br.com.sales.micro.dto.request.ProductBarCodeListDto;
import br.com.sales.micro.dto.response.OperationHttpStatusCodeDto;
import br.com.sales.micro.dto.response.payment.PaymentDto;
import br.com.sales.micro.event.dto.SetSaleEventDto;
import br.com.sales.micro.dto.response.UserDto;
import br.com.sales.micro.dto.response.ProductDto;
import br.com.sales.micro.event.producer.SaleEventProducer;
import br.com.sales.micro.exception.*;
import br.com.sales.micro.exception.user.IncompatibleUserDataException;
import br.com.sales.micro.exception.user.UserNotFoundException;
import br.com.sales.micro.exception.user.ErrorRetrievingUserDataException;
import br.com.sales.micro.exception.product.ErrorRetrievingProductDataException;
import br.com.sales.micro.exception.product.ProductDataIncompatibleException;
import br.com.sales.micro.exception.product.ProductNotFoundException;
import br.com.sales.micro.respository.ICanceledRepository;
import br.com.sales.micro.respository.ICompletedRepository;
import br.com.sales.micro.respository.ISaleRepository;
import br.com.sales.micro.service.IUserClient;
import br.com.sales.micro.service.IPaymentClient;
import br.com.sales.micro.service.ISaleService;
import br.com.sales.micro.service.IProductClient;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
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
    private final IUserClient IUserClient;
    private final SaleEventProducer saleEventProducer;
    private final ICanceledRepository iCanceledRepository;
    private final IPaymentClient iPaymentClient;
    private final ICompletedRepository iCompletedRepository;

    public SaleService(
            ISaleRepository iSaleRepository,
            IProductClient IProductClient,
            IUserClient IUserClient,
            SaleEventProducer saleEventProducer,
            ICanceledRepository iCanceledRepository,
            IPaymentClient iPaymentClient,
            ICompletedRepository iCompletedRepository
    ) {
        this.iSaleRepository = iSaleRepository;
        this.IProductClient = IProductClient;
        this.IUserClient = IUserClient;
        this.saleEventProducer = saleEventProducer;
        this.iCanceledRepository = iCanceledRepository;
        this.iPaymentClient = iPaymentClient;
        this.iCompletedRepository = iCompletedRepository;
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
    public UserDto getUserData(String id) {
        try {
            return IUserClient.getUserData(id);
        } catch (RetryableException e) {
            throw new ServiceUnavailableException("User Microservice");
        } catch (FeignException e) {
            switch (e.status()) {
                case 404:
                    throw new UserNotFoundException();
                case 400:
                    throw new IncompatibleUserDataException();
                default:
                    throw new ErrorRetrievingUserDataException();
            }
        }
    }

    @Override
    public Sale makeSale(
            String userId,
            String userCpf,
            Double totalValue,
            List<Item> products
    ) {
        User user = User.builder()
                .id(userId)
                .cpf(userCpf)
                .build();

        Sale sale = Sale.builder()
                .user(user)
                .status(Status.CREATED)
                .date(LocalDateTime.now())
                .totalValue(totalValue)
                .items(products)
                .created_at(LocalDateTime.now())
                .build();

        Sale newSale = iSaleRepository.save(sale);
        saleEventProducer.setSaleEvent(new SetSaleEventDto(
                newSale.getId(),
                newSale.getUser().getId(),
                newSale.getStatus(),
                newSale.getItems()
        ));

        return newSale;
    }

    @Override
    public Sale getSaleInfo(String id) {
        return iSaleRepository.findById(id).orElseThrow(SaleNotFoundException::new);
    }

    @Override
    public OperationHttpStatusCodeDto cancelSale(String saleId, String userId) {
        Optional<Sale> saleResponse = iSaleRepository.findById(saleId);

        if (saleResponse.isEmpty()) return this.cancelCompletedSale(saleId, userId);

        Sale sale = saleResponse.get();
        String saleUserId = sale.getUser().getId();

        if (!saleUserId.equals(userId)) throw new PermissionDeniedException();

        iSaleRepository.deleteById(saleId);

        sale.setStatus(Status.CANCELED);
        Canceled canceled = Canceled.builder()
                .sale(sale)
                .created_at(LocalDateTime.now())
                .build();

        iCanceledRepository.save(canceled);
        saleEventProducer.setSaleEvent(new SetSaleEventDto(
                saleId,
                userId,
                Status.CANCELED,
                sale.getItems()
        ));

        return new OperationHttpStatusCodeDto("Sale canceled successfully!", HttpStatus.OK);
    }

    public OperationHttpStatusCodeDto cancelCompletedSale(String saleId, String userId) {
        Completed sale = iCompletedRepository.findBySaleId(saleId).orElseThrow(SaleNotFoundException::new);
        String saleUserId = sale.getSale().getUser().getId();

        if (!saleUserId.equals(userId)) throw new PermissionDeniedException();

        PaymentDto payment = iPaymentClient.getPaymentInfo(saleId);
        Instant dateApproved = payment.payment().payment().dateApproved();
        boolean mustOfTwoHoursOld = Duration
                .between(dateApproved, Instant.now())
                .abs()
                .compareTo(Duration.ofHours(2)) >= 0;

        if (mustOfTwoHoursOld) throw new PermissionDeniedException("The cancellation is no longer possible!");

        sale.getSale().setStatus(Status.CANCELED);
        Canceled canceledSale = Canceled.builder()
                .sale(sale.getSale())
                .created_at(LocalDateTime.now())
                .build();

        iCanceledRepository.save(canceledSale);
        iCompletedRepository.deleteById(sale.getId());
        saleEventProducer.setSaleEvent(new SetSaleEventDto(
                saleId,
                userId,
                Status.CANCELED,
                sale.getSale().getItems()
        ));

        return new OperationHttpStatusCodeDto("The sale cancellation is being processed!", HttpStatus.ACCEPTED);
    }
}
