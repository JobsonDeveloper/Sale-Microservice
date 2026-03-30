package br.com.sales.micro.exception;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException() {
        super("Sale not found!");
    }
    public SaleNotFoundException(String message) {
        super(message);
    }
}
