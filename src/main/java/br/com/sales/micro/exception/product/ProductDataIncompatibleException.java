package br.com.sales.micro.exception.product;

public class ProductDataIncompatibleException extends RuntimeException {
    public ProductDataIncompatibleException() {
        super("Incompatible product data!");
    }
    public ProductDataIncompatibleException(String message) {
        super(message);
    }
}
