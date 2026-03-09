package br.com.sales.micro.exception.client;

public class ErrorRetrievingClientDataException extends RuntimeException {
    public ErrorRetrievingClientDataException() {
        super("It was not possible to get client data!");
    }
    public ErrorRetrievingClientDataException(String message) {
        super(message);
    }
}
