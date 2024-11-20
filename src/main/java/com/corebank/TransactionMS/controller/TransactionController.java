package com.corebank.TransactionMS.controller;

import com.corebank.TransactionMS.model.Transaction;
import com.corebank.TransactionMS.service.TransactionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transacciones/")
public class TransactionController {

    @Autowired
    public TransactionService transactionService;

    //Endpoint para realizar un deposito
    @PostMapping("/deposito/{accountNumber}")
    public Mono<ResponseEntity<Transaction>> registerDeposit(@PathVariable String accountNumber, @RequestParam double amount) {
        return transactionService.registerDeposit(accountNumber, amount)
                .map(transaction -> ResponseEntity.ok(transaction));
    }

    // Endpoint para realizar un retiro
    @PostMapping("/retiro/{accountNumber}")
    public Mono<ResponseEntity<Transaction>> registerWithdrawal(@PathVariable String accountNumber, @RequestParam double amount) {
        return transactionService.registerWithdrawal(accountNumber, amount)
                .map(transaction -> ResponseEntity.ok(transaction));
    }

    // Endpoint para realizar una transferencia entre dos cuentas
    @PostMapping("/transferencia")
    public Mono<ResponseEntity<Transaction>> registerTransfer(@RequestParam String sourceAccount, @RequestParam String destinationAccount, @RequestParam double amount) {
        return transactionService.registerTransfer(sourceAccount, destinationAccount, amount)
                .map(transaction -> ResponseEntity.ok(transaction));
    }

    //Endpoint para ver el historial de las transferencias
    @GetMapping("/historial")
    public Flux<Transaction> viewTransactions(@RequestHeader Map<String, String> headers) {
        return transactionService.listTransactions();
    }

}
