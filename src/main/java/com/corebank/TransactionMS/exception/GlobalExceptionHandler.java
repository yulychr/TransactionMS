package com.corebank.TransactionMS;

import com.corebank.TransactionMS.exception.AccountNotFoundException;
import com.corebank.TransactionMS.exception.InsufficientFundsException;
import com.corebank.TransactionMS.exception.InvalidTransferAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidTransferAmountException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidAmount(InvalidTransferAmountException ex) {
        String message = ex.getMessage();
        ErrorResponse errorResponse = new InvalidTransferAmountException("Invalid transfer amount", message);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccountNotFound(AccountNotFoundException ex) {
        String errorMessage = ex.getMessage();
        ErrorResponse errorResponse = new AccountNotFoundException("Account not found", errorMessage);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponse errorResponse = new InsufficientFundsException("Insufficient funds", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", "An unexpected error occurred.");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}

