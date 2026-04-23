package br.com.sales.micro.exception.user;

public class IncompatibleUserDataException extends RuntimeException {
    public IncompatibleUserDataException() {
        super("Incompatible user data!");
    }
    public IncompatibleUserDataException(String message) {
        super(message);
    }
}
