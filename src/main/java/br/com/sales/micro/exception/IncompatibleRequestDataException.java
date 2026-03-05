package br.com.sales.micro.exception;

public class IncompatibleRequestDataException extends RuntimeException {
    public IncompatibleRequestDataException() {
        super("Incompatible request data exception!");
    }
    public IncompatibleRequestDataException(String message) {
        super(message);
    }
}
