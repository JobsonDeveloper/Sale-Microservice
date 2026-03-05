package br.com.sales.micro.exception;

public class ErrorCreatingTheSaleException extends RuntimeException {
    public ErrorCreatingTheSaleException() {
        super("Error creating the sale!");
    }
    public ErrorCreatingTheSaleException(String message) {
        super(message);
    }
}
