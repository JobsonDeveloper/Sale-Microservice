package br.com.sales.micro.service.imp;

import br.com.sales.micro.domain.Canceled;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.event.dto.SetSaleEventDto;
import br.com.sales.micro.event.producer.SaleEventProducer;
import br.com.sales.micro.exception.ErrorCancelingSaleException;
import br.com.sales.micro.exception.PermissionDeniedException;
import br.com.sales.micro.exception.SaleNotFoundException;
import br.com.sales.micro.respository.ICanceledRepository;
import br.com.sales.micro.respository.ISaleRepository;
import br.com.sales.micro.service.ICanceledService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CanceledService implements ICanceledService {
    private final ICanceledRepository iCanceledRepository;
    private final ISaleRepository iSaleRepository;
    private final SaleEventProducer saleEventProducer;

    public CanceledService(ICanceledRepository iCanceledRepository, ISaleRepository iSaleRepository, SaleEventProducer saleEventProducer) {
        this.iCanceledRepository = iCanceledRepository;
        this.iSaleRepository = iSaleRepository;
        this.saleEventProducer = saleEventProducer;
    }

    @Override
    public void cancelSale(String saleId, String clientId) {
        Optional<Sale> registeredSale = iSaleRepository.findById(saleId);

        if (!registeredSale.isPresent()) {
            throw new SaleNotFoundException();
        }

        Sale sale = registeredSale.get();
        Status saleStatus = sale.getStatus();
        String saleClientId = sale.getClient().getId();

        if (!saleClientId.equals(clientId)) {
            throw new PermissionDeniedException();
        }

        if (saleStatus.equals(Status.PAID) || saleStatus.equals(Status.DELIVERED)) {
            throw new PermissionDeniedException("It is no longer possible to cancel the sale!");
        }

        iSaleRepository.deleteById(saleId);

        Canceled canceled = Canceled.builder()
                .status(Status.CANCELED)
                .date(sale.getDate())
                .totalValue(sale.getTotalValue())
                .client(sale.getClient())
                .items(sale.getItems())
                .created_at(LocalDateTime.now())
                .build();

        Canceled canceledSale = iCanceledRepository.save(canceled);

        if (canceledSale.getId() == null) {
            throw new ErrorCancelingSaleException();
        }

        saleEventProducer.setSaleEvent(new SetSaleEventDto(
                saleId,
                clientId,
                Status.CANCELED,
                canceledSale.getItems()
        ));
    }
}
