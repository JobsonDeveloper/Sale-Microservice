package br.com.sales.micro.dto.response;

public class PurchaseNotPaidException extends RuntimeException {
    public PurchaseNotPaidException() {
        super("The purchase payment was not completed!");
    }
    public PurchaseNotPaidException(String message) {
        super(message);
    }
}
