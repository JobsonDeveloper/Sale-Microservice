package br.com.sales.micro.service;

import br.com.sales.micro.domain.Item;
import br.com.sales.micro.domain.Sale;
import br.com.sales.micro.dto.response.UserDto;
import br.com.sales.micro.dto.response.OperationHttpStatusCodeDto;
import br.com.sales.micro.dto.response.ProductDto;

import java.util.List;

public interface ISaleService {
    public ProductDto getProductsData(List<Long> barCodes);
    public UserDto getUserData(String id);
    public Sale makeSale(String userId, String userCpf, Double totalValue, List<Item> products);
    public Sale getSaleInfo(String id);
    public OperationHttpStatusCodeDto cancelSale(String saleId, String userId);
}
