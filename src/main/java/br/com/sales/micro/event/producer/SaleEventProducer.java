package br.com.sales.micro.event.producer;

import br.com.sales.micro.event.dto.SetSaleEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SaleEventProducer {
    private final KafkaTemplate<String, SetSaleEventDto> kafkaTemplate;

    public SaleEventProducer(KafkaTemplate<String, SetSaleEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void setSaleEvent(SetSaleEventDto saleInfo) {
        kafkaTemplate.send("sale", saleInfo);
    }
}
