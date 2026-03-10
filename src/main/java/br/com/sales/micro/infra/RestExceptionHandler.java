package br.com.sales.micro.infra;

import br.com.sales.micro.exception.*;
import br.com.sales.micro.exception.client.ClientDataIncompatibleException;
import br.com.sales.micro.exception.client.ClientNotFoundException;
import br.com.sales.micro.exception.client.ErrorRetrievingClientDataException;
import br.com.sales.micro.exception.product.ErrorRetrievingProductDataException;
import br.com.sales.micro.exception.product.InsufficientProductsException;
import br.com.sales.micro.exception.product.ProductDataIncompatibleException;
import br.com.sales.micro.exception.product.ProductNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private Map<String, String> mapError(FieldError fieldError) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("field", fieldError.getField());
        mapping.put("message", fieldError.getDefaultMessage());

        return mapping;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");

        List<Map<String, String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapError)
                .toList();

        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InconsistentValueException.class)
    private ResponseEntity<DefaultErrorResponse> inconsistentValueHandler(InconsistentValueException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(defaultErrorResponse);
    }

    @ExceptionHandler(ErrorCreatingTheSaleException.class)
    private ResponseEntity<DefaultErrorResponse> errorCreatingTheSaleHandler(ErrorCreatingTheSaleException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(defaultErrorResponse);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    private ResponseEntity<DefaultErrorResponse> serviceUnavailableHandler(ServiceUnavailableException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(defaultErrorResponse);
    }

    @ExceptionHandler(SaleNotFoundException.class)
    private ResponseEntity<DefaultErrorResponse> saleNotFoundHandler(SaleNotFoundException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    private ResponseEntity<DefaultErrorResponse> productNotFoundHandler(ProductNotFoundException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorResponse);
    }

    @ExceptionHandler(ProductDataIncompatibleException.class)
    private ResponseEntity<DefaultErrorResponse> productDataIncompatibleDataHandler(ProductDataIncompatibleException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorResponse);
    }

    @ExceptionHandler(ErrorRetrievingProductDataException.class)
    private ResponseEntity<DefaultErrorResponse> errorRetrievingProductDataHandler(ErrorRetrievingProductDataException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(defaultErrorResponse);
    }

    @ExceptionHandler(InsufficientProductsException.class)
    private ResponseEntity<DefaultErrorResponse> insufficientProductsHandler(InsufficientProductsException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(defaultErrorResponse);
    }

    @ExceptionHandler(ErrorRetrievingClientDataException.class)
    private ResponseEntity<DefaultErrorResponse> errorRetrievingClientDataHandler(ErrorRetrievingClientDataException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.BAD_GATEWAY, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(defaultErrorResponse);
    }

    @ExceptionHandler(ClientDataIncompatibleException.class)
    private ResponseEntity<DefaultErrorResponse> clientDataIncompatibleDataHandler(ClientDataIncompatibleException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(defaultErrorResponse);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    private ResponseEntity<DefaultErrorResponse> clientNotFoundHandler(ClientNotFoundException exception) {
        DefaultErrorResponse defaultErrorResponse = new DefaultErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultErrorResponse);
    }
}
