package com.corebank.TransactionMS.service;

import com.corebank.TransactionMS.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    public Flux<Transaction> listTransactions();
    public Mono<Transaction> registerDeposit(String accountNumber, double amount);
    public Mono<Transaction> registerWithdrawal(String accountNumber, double amount);
    public Mono<Transaction> registerTransfer(String sourceAccount, String destinationAccount, double amount);



}
