package br.com.sales.micro.service;

import br.com.sales.micro.domain.Completed;

public interface ICompletedService {
    public Completed markSaleAsCompleted(String saleId);
}
