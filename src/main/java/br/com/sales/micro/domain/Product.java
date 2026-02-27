package br.com.sales.micro.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "Products")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    public String id;
    public String name;
    private Long barCode;
    private String brand;
    private Double weight;
    private Long quantity;
    private Double value;
    private String classification;
    private String description;
    private LocalDateTime manufacturing;
    private LocalDateTime expiration;
}
