package br.com.sales.micro.exception;

public class ErrorMarkingTheSaleAsCompletedException extends RuntimeException {
    public ErrorMarkingTheSaleAsCompletedException() {
        super("It was not possible to mark the sale as completed!");
    }

    public ErrorMarkingTheSaleAsCompletedException(String message) {
        super(message);
    }
}
