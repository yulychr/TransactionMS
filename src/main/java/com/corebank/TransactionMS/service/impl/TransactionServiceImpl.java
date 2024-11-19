package com.corebank.TransactionMS.service.impl;

import com.corebank.TransactionMS.model.Account;
import com.corebank.TransactionMS.model.Transaction;
import com.corebank.TransactionMS.repository.AccountRepository;
import com.corebank.TransactionMS.repository.TransactionRepository;
import com.corebank.TransactionMS.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Flux<Transaction>listTransactions(){
        return transactionRepository.findAll();
    }

    @Override
    public Mono<Transaction> registerDeposit(String accountNumber, double amount) {
        return accountRepository.findByAccountNumber(accountNumber)
                .flatMap(account -> {
                    account.setBalance(account.getBalance() + amount);
                    return accountRepository.save(account)
                            .flatMap(savedAccount -> createTransaction("deposito", amount, savedAccount, null));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("Anuncio: La cuenta con número " + accountNumber + " no fue encontrada.");
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Transaction> registerWithdrawal(String accountNumber, double amount) {
        return accountRepository.findByAccountNumber(accountNumber)
                .flatMap(account -> {
                    if (account.getBalance() >= amount) {
                        account.setBalance(account.getBalance() - amount);
                        return accountRepository.save(account)
                                .flatMap(savedAccount -> createTransaction("retiro", amount, savedAccount, null));
                    } else {
                        // Error de saldo insuficiente
                        return Mono.error(new IllegalArgumentException("Saldo insuficiente"));
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cuenta no encontrada")));
    }

    @Override
    public Mono<Transaction> registerTransfer(String sourceAccount, String destinationAccount, double amount) {
        return accountRepository.findByAccountNumber(sourceAccount)
                .flatMap(accountSource -> accountRepository.findByAccountNumber(destinationAccount)
                        .flatMap(accountDestination -> {
                            if (accountSource.getBalance() >= amount) {
                                accountSource.setBalance(accountSource.getBalance() - amount);
                                accountDestination.setBalance(accountDestination.getBalance() + amount);
                                return accountRepository.save(accountSource)
                                        .then(accountRepository.save(accountDestination))
                                        .flatMap(savedAccountSource -> createTransaction("transferencia", amount, savedAccountSource, accountDestination));
                            } else {
                                return Mono.error(new IllegalArgumentException("Saldo insuficiente en la cuenta de origen"));
                            }
                        }));

    }

    private Mono<Transaction> createTransaction(String type, double amount, Account sourceAccount, Account destinationAccount) {
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setSourceAccount(sourceAccount.getId());
        if (destinationAccount != null) {
            transaction.setDestinationAccount(destinationAccount.getId());
        }
        return transactionRepository.save(transaction);
    }


}
