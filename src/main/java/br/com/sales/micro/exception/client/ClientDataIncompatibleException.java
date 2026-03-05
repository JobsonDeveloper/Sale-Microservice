package br.com.sales.micro.exception.client;

public class ClientDataIncompatibleException extends RuntimeException {
    public ClientDataIncompatibleException() {
        super("Incompatible client data!");
    }
    public ClientDataIncompatibleException(String message) {
        super(message);
    }
}
