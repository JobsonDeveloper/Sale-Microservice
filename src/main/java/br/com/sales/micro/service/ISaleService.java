package br.com.sales.micro.service;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Sale;

import java.util.List;

public interface ISaleService {
    public Sale makeSale(String clientId, Double totalValue, List<Item> products);
}
