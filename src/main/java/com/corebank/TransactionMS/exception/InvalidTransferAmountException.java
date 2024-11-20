package com.corebank.TransactionMS.exception;

public class InvalidTransferAmountException extends RuntimeException{
    public InvalidTransferAmountException(String message) {
        super(message);
    }
}