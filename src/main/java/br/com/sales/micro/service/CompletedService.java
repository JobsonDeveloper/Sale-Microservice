package br.com.sales.micro.service;

import br.com.sales.micro.domain.Completed;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.dto.response.PurchaseNotPaidException;
import br.com.sales.micro.event.dto.SetSaleEventDto;
import br.com.sales.micro.event.producer.SaleEventProducer;
import br.com.sales.micro.exception.ErrorMarkingTheSaleAsCompletedException;
import br.com.sales.micro.exception.SaleNotFoundException;
import br.com.sales.micro.respository.ICompletedRepository;
import br.com.sales.micro.respository.ISaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CompletedService implements ICompletedService {
    private final ICompletedRepository iCompletedRepository;
    private final ISaleRepository iSaleRepository;
    private final SaleEventProducer saleEventProducer;

    public CompletedService(
            ICompletedRepository iCompletedRepository,
            ISaleRepository iSaleRepository, SaleEventProducer saleEventProducer
    ) {
        this.iCompletedRepository = iCompletedRepository;
        this.iSaleRepository = iSaleRepository;
        this.saleEventProducer = saleEventProducer;
    }

    @Override
    public Completed markSaleAsCompleted(String saleId) {
        Optional<Sale> registeredSale = iSaleRepository.findById(saleId);

        if (!registeredSale.isPresent()) {
            throw new SaleNotFoundException();
        }

        Sale sale = registeredSale.get();

        if (!sale.getStatus().equals(Status.PAID)) {
            throw new PurchaseNotPaidException();
        }

        iSaleRepository.deleteById(saleId);
        sale.setStatus(Status.DELIVERED);

        Completed completed = Completed.builder()
                .sale(sale)
                .created_at(LocalDateTime.now())
                .build();

        Completed completedSale = iCompletedRepository.save(completed);

        if (completedSale.getId() == null) {
            throw new ErrorMarkingTheSaleAsCompletedException();
        }

        saleEventProducer.setSaleEvent(new SetSaleEventDto(
                sale.getId(),
                sale.getClient().getId(),
                Status.DELIVERED,
                sale.getItems()
        ));

        return completedSale;
    }
}
