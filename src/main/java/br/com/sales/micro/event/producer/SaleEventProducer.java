package br.com.sales.micro.event.producer;

import br.com.sales.micro.event.dto.SaleStartedEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SaleEventProducer {
    private final KafkaTemplate<String, SaleStartedEventDto> kafkaTemplate;

    public SaleEventProducer(KafkaTemplate<String, SaleStartedEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void saleStartedEvent(SaleStartedEventDto saleInfo) {
        kafkaTemplate.send("sale-created", saleInfo);
    }
}
