package br.com.sales.micro.event.consumer;

import br.com.sales.micro.domain.Completed;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.event.dto.PaymentPendingEventDto;
import br.com.sales.micro.exception.ErrorDeletingSaleException;
import br.com.sales.micro.exception.ErrorTransferringSalesDataToCompleted;
import br.com.sales.micro.exception.SaleNotFoundException;
import br.com.sales.micro.respository.ICompletedRepository;
import br.com.sales.micro.respository.ISaleRepository;
import org.springframework.cglib.core.Local;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SaleConsumer {
    private final ISaleRepository iSaleRepository;
    private final ICompletedRepository iCompletedRepository;

    public SaleConsumer(ISaleRepository iSaleRepository, ICompletedRepository iCompletedRepository) {
        this.iSaleRepository = iSaleRepository;
        this.iCompletedRepository = iCompletedRepository;
    }

    @KafkaListener(topics = "payment", groupId = "sale-group")
    public void salePaymentProcesses(PaymentPendingEventDto event) {
        Status status = event.status();
        String saleId = event.saleId();

        if (status.equals(Status.PENDING_PAYMENT)) {

            Optional<Sale> sale = iSaleRepository.findById(saleId);

            if (!sale.isPresent()) throw new SaleNotFoundException();

            if (!status.equals(sale.get().getStatus())) {
                sale.get().setStatus(status);
                sale.get().setUpdated_at(LocalDateTime.now());
                iSaleRepository.save(sale.get());
            }
        }
        if (status.equals(Status.PAID)) {
            Optional<Sale> sale = iSaleRepository.findById(saleId);

            if (!sale.isPresent()) throw new SaleNotFoundException();

            iSaleRepository.deleteById(sale.get().getId());
            Optional<Sale> deletedSale = iSaleRepository.findById(saleId);

            if(deletedSale.isPresent()) throw new ErrorDeletingSaleException();

            sale.get().setStatus(Status.PAID);
            Completed completedSale = Completed.builder()
                    .sale(sale.get())
                    .created_at(LocalDateTime.now())
                    .build();

            Completed completed = iCompletedRepository.save(completedSale);

            if(completed.getId() == null) throw new ErrorTransferringSalesDataToCompleted();
        }
    }
}
