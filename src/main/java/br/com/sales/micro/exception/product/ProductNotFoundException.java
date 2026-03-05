package br.com.sales.micro.exception.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Product not found!");
    }
    public ProductNotFoundException(String message) {
        super(message);
    }
}
