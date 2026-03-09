package br.com.sales.micro.exception;

public class InconsistentValueException extends RuntimeException {
    public InconsistentValueException() {
        super("The amount to be paid is inconsistent!");
    }
    public InconsistentValueException(String message) {
        super(message);
    }
}
