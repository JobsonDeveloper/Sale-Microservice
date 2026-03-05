package br.com.sales.micro.exception;

public class InconsistentValueException extends RuntimeException {
    public InconsistentValueException() {
        super("Total value is inconsistent!");
    }
    public InconsistentValueException(String message) {
        super(message);
    }
}
