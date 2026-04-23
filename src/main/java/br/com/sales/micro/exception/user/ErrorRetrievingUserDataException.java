package br.com.sales.micro.exception.user;

public class ErrorRetrievingUserDataException extends RuntimeException {
    public ErrorRetrievingUserDataException() {
        super("It was not possible to get user data!");
    }
    public ErrorRetrievingUserDataException(String message) {
        super(message);
    }
}
