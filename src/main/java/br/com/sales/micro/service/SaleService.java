package br.com.sales.micro.service;

import br.com.sales.micro.respository.ISaleRespository;
import org.springframework.stereotype.Service;

@Service
public class SaleService implements ISaleService {
    private final ISaleRespository saleRespository;

    public SaleService(ISaleRespository saleRespository) {this.saleRespository = saleRespository;}
}
