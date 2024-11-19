package com.corebank.TransactionMS.controller;

import com.corebank.TransactionMS.model.Transaction;
import com.corebank.TransactionMS.service.TransactionService;
import java.util.Map;

import com.corebank.TransactionMS.service.impl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transacciones/")
public class TransactionController {

    @Autowired
    public TransactionService transactionService;

    @PostMapping("/depositar/{accountNumber}")
    public Mono<Transaction> registerDeposit(@PathVariable String accountNumber, @RequestParam double amount) {
        return transactionService.registerDeposit(accountNumber, amount);
    }

    // Endpoint para realizar un retiro
    @PostMapping("/retirar/{accountNumber}")
    public Mono<Transaction> registerWithdrawal(@PathVariable String accountNumber, @RequestParam double amount) {
        return transactionService.registerWithdrawal(accountNumber, amount);
    }

    // Endpoint para realizar una transferencia entre dos cuentas
    @PostMapping("/transferir")
    public Mono<Transaction> registerTransfer(@RequestParam String sourceAccount, @RequestParam String destinationAccount, @RequestParam double amount) {
        return transactionService.registerTransfer(sourceAccount, destinationAccount, amount);
    }


    @GetMapping("/historial")
    public Flux<Transaction> viewTransactions(@RequestHeader Map<String, String> headers) {
        return transactionService.listTransactions();
    }

}
