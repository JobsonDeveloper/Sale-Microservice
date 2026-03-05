package br.com.sales.micro.exception.product;

public class ErrorRetrievingProductDataException extends RuntimeException {
    public ErrorRetrievingProductDataException() {
        super("It was not possible to get product data!");
    }
    public ErrorRetrievingProductDataException(String message) {
        super(message);
    }
}
