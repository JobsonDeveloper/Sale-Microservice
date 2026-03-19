package br.com.sales.micro.exception;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException () {
        super("Permission denied! Sale not linked to this user!");
    }

    public PermissionDeniedException (String message) {
        super(message);
    }
}
