package br.com.sales.micro.exception;

public class ErrorDeletingSaleException extends RuntimeException {
    public ErrorDeletingSaleException()  {super("It was not possible to delete the sale!");}
    public ErrorDeletingSaleException(String message) {
        super(message);
    }
}
