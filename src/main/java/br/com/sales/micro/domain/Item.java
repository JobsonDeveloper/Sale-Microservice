package br.com.sales.micro.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private String id;
    private String name;
    private Long barCode;
    private Double value;
    private Long quantity;
}
