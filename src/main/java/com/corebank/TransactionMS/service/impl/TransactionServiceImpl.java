package com.corebank.TransactionMS.service.impl;

import com.corebank.TransactionMS.exception.AccountNotFoundException;
import com.corebank.TransactionMS.exception.InsufficientFundsException;
import com.corebank.TransactionMS.exception.InvalidTransferAmountException;
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

    //Metodo para registrar el deposito
    @Override
    public Mono<Transaction> registerDeposit(String accountNumber, double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransferAmountException("Invalid deposit amount. Amount must be positive."));
        }
        return accountRepository.findByAccountNumber(accountNumber)
                .flatMap(account -> {
                    account.setBalance(account.getBalance() + amount);
                    return accountRepository.save(account)
                            .flatMap(savedAccount ->
                                    createTransaction("deposito", amount, savedAccount, null)
                            );
                })
                .switchIfEmpty(Mono.defer(() -> {
                    return Mono.error(new AccountNotFoundException("Account with number " + accountNumber + " not found."));
                }));
    }

    //Metodo para reistrar el retiro
    @Override
    public Mono<Transaction> registerWithdrawal(String accountNumber, double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransferAmountException("Invalid withdrawal amount. Amount must be positive"));
        }

        return accountRepository.findByAccountNumber(accountNumber)
                .flatMap(account -> {
                    if (account.getBalance() >= amount) {
                        account.setBalance(account.getBalance() - amount);
                        return accountRepository.save(account)
                                .flatMap(savedAccount -> createTransaction("retiro", amount, savedAccount, null));
                    } else {
                        return Mono.error(new InsufficientFundsException("Withdrawal amount exceeds available balance."));
                    }
                })
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Account with number " + accountNumber + " not found.")));
    }

    @Override
    public Mono<Transaction> registerTransfer(String sourceAccount, String destinationAccount, double amount) {
        if (amount <= 0) {
            return Mono.error(new InvalidTransferAmountException("Invalid transfer amount. Amount must be positive.")); // Lanzamos una excepción si el monto es inválido
        }

        return accountRepository.findByAccountNumber(sourceAccount)
                .switchIfEmpty(Mono.error(new AccountNotFoundException("Source account not found."))) // Si no se encuentra la cuenta fuente
                .flatMap(accountSource -> accountRepository.findByAccountNumber(destinationAccount)
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Destination account not found."))) // Si no se encuentra la cuenta destino
                        .flatMap(accountDestination -> {
                            if (accountSource.getBalance() >= amount) {
                                accountSource.setBalance(accountSource.getBalance() - amount);
                                accountDestination.setBalance(accountDestination.getBalance() + amount);
                                return accountRepository.save(accountSource)
                                        .then(accountRepository.save(accountDestination))
                                        .flatMap(savedAccountSource -> createTransaction("transferencia", amount, savedAccountSource, accountDestination));
                            } else {
                                return Mono.error(new InsufficientFundsException("The source account does not have enough balance to complete the transfer.")); // Fondos insuficientes
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
