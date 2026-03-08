package br.com.sales.micro.events.producer;

import br.com.sales.micro.events.dto.SaleStartedEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SaleIventProducer {
    private final KafkaTemplate<String, SaleStartedEventDto> kafkaTemplate;

    public SaleIventProducer(KafkaTemplate<String, SaleStartedEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void saleStartedEvent(SaleStartedEventDto saleInfo) {
        kafkaTemplate.send("sale-topic", saleInfo);
    }
}
