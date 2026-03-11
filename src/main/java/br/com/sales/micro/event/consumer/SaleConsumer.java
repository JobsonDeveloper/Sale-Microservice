package br.com.sales.micro.event.consumer;

import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.domain.Status;
import br.com.sales.micro.event.dto.PaymentPendingEventDto;
import br.com.sales.micro.exception.SaleNotFoundException;
import br.com.sales.micro.respository.ISaleRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SaleConsumer {
    private final ISaleRepository iSaleRepository;

    public SaleConsumer(ISaleRepository iSaleRepository) {
        this.iSaleRepository = iSaleRepository;
    }

    @KafkaListener(topics = "payment", groupId = "sale-group")
    public void consume(PaymentPendingEventDto event) {
        Status status = event.status();
        Status pending = Status.PENDING_PAYMENT;
        Status paid = Status.PAID;

        if ((status.equals(pending)) || (status.equals(paid))) {
            String saleId = event.saleId();

            Optional<Sale> sale = iSaleRepository.findById(saleId);

            if (!sale.isPresent()) {
                throw new SaleNotFoundException();
            }

            if (!status.equals(sale.get().getStatus())) {
                sale.get().setStatus(status);
                iSaleRepository.save(sale.get());
            }
        }
    }
}
