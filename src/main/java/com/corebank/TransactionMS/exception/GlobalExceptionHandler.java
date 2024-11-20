package com.corebank.TransactionMS.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de InvalidTransferAmountException
    @ExceptionHandler(InvalidTransferAmountException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidTransferAmountException(InvalidTransferAmountException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return Mono.just(ResponseEntity.badRequest().body(errorResponse)); // 400 Bad Request
    }

    // Manejo de AccountNotFoundException
    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccountNotFoundException(AccountNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)); // 404 Not Found
    }

    // Manejo de InsufficientFundsException
    @ExceptionHandler(InsufficientFundsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)); // 400 Bad Request
    }

    // Manejo de excepciones genéricas
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred.");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)); // 500 Internal Server Error
    }

    // Clase interna para la respuesta de error
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}


