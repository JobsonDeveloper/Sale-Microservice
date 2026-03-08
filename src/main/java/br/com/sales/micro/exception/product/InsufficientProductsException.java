package br.com.sales.micro.exception.product;

public class InsufficientProductsException extends RuntimeException {
    public InsufficientProductsException() {
        super("Insufficient products in stock!");
    }
    public InsufficientProductsException(String message) {
        super(message);
    }
}