package br.com.sales.micro.exception;

public class SaleAlreadyCancelledException extends RuntimeException {
    public SaleAlreadyCancelledException() {super("The sale has already been cancelled previously!");}
    public SaleAlreadyCancelledException(String message) {
        super(message);
    }
}
