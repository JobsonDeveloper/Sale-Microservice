package br.com.sales.micro.event.consumer;

import br.com.sales.micro.domain.Completed;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.event.dto.PaymentPendingEventDto;
import br.com.sales.micro.exception.SaleNotFoundException;
import br.com.sales.micro.respository.ICompletedRepository;
import br.com.sales.micro.respository.ISaleRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SaleConsumer {
    private final ISaleRepository iSaleRepository;
    private final ICompletedRepository iCompletedRepository;

    public SaleConsumer(ISaleRepository iSaleRepository, ICompletedRepository iCompletedRepository) {
        this.iSaleRepository = iSaleRepository;
        this.iCompletedRepository = iCompletedRepository;
    }

    @KafkaListener(topics = "payment", groupId = "${spring.kafka.consumer.group-id}")
    public void paymentConsumer(PaymentPendingEventDto event) {
        Status status = event.status();
        String saleId = event.saleId();

        if (status.equals(Status.PENDING_PAYMENT)) this.changeSaleStatus(saleId, status);
        if (status.equals(Status.PAID)) this.markAsCompleted(saleId);
    }

    public void changeSaleStatus(String saleId, Status status) {
        Sale sale = iSaleRepository.findById(saleId).orElseThrow(SaleNotFoundException::new);

        if (!status.equals(sale.getStatus())) {
            sale.setStatus(status);
            sale.setUpdated_at(LocalDateTime.now());
            iSaleRepository.save(sale);
        }
    }

    public void markAsCompleted(String saleId) {
        Sale sale = iSaleRepository.findById(saleId).orElseThrow(SaleNotFoundException::new);

        iSaleRepository.deleteById(sale.getId());

        sale.setStatus(Status.PAID);
        Completed completedSale = Completed.builder()
                .sale(sale)
                .created_at(LocalDateTime.now())
                .build();

        iCompletedRepository.save(completedSale);
    }
}
