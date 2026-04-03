package br.com.sales.micro.exception;

public class ErrorTransferringSalesDataToCompleted extends RuntimeException {
  public ErrorTransferringSalesDataToCompleted() {
    super("It was not possible to transfer the sale data to completed table!");
  }
    public ErrorTransferringSalesDataToCompleted(String message) {
        super(message);
    }
}
