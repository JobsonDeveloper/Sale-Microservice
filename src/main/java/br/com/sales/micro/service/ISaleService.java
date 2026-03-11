package br.com.sales.micro.service;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.dto.response.ClientDto;
import br.com.sales.micro.dto.response.ProductDto;

import java.util.List;

public interface ISaleService {
    public ProductDto getProductsData(List<Long> barCodes);
    public ClientDto getClientData(String id);
    public Sale makeSale(String clientId, Long clientCpf, Double totalValue, List<Item> products);
    public Sale getSaleInfo(String id);
}
