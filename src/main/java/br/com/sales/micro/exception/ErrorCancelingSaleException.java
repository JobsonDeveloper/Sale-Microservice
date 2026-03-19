package br.com.sales.micro.exception;

public class ErrorCancelingSaleException extends RuntimeException {
    public ErrorCancelingSaleException() {
        super("It was not possible to cancel the sale!");
    }
    public ErrorCancelingSaleException(String message) {
        super(message);
    }
}
