package br.com.sales.micro.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private String id;
    private String name;
    private Long barCode;
    private String description;
    private Integer quantity;
    private Double value;
}
