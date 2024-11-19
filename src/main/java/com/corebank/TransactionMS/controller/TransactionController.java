package com.corebank.TransactionMS.controller;

import com.corebank.TransactionMS.model.Transaction;
import com.corebank.TransactionMS.service.TransactionService;
import java.util.Map;
import java.util.Objects;

import com.corebank.TransactionMS.service.impl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/deposito/{accountNumber}")
    public Mono<ResponseEntity<Transaction>> registerDeposit(@PathVariable String accountNumber, @RequestParam double amount) {
        return transactionService.registerDeposit(accountNumber, amount)
                .map(transaction -> ResponseEntity.ok(transaction))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null)));
    }

    // Endpoint para realizar un retiro
    @PostMapping("/retiro/{accountNumber}")
    public Mono<ResponseEntity<Transaction>> registerWithdrawal(@PathVariable String accountNumber, @RequestParam double amount) {
        return transactionService.registerWithdrawal(accountNumber, amount)
                .map(transaction -> {
                    // Si la transacción es exitosa, retornamos 200 OK con el Transaction
                    return ResponseEntity.ok(transaction);
                })
                .onErrorResume(ex -> {
                    // Aquí manejamos los errores
                    if (ex instanceof IllegalArgumentException) {
                        String errorMessage = ex.getMessage();
                        if (errorMessage.contains("Cuenta no encontrada")) {
                            // Si la cuenta no se encuentra, retornamos 404 Not Found con Transaction vacío
                            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(null));  // Null ya que no se puede retornar un Transaction en este caso
                        } else if (errorMessage.contains("Saldo insuficiente")) {
                            // Si el saldo es insuficiente, retornamos 422 Unprocessable Entity con Transaction vacío
                            return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                    .body(null));  // Null ya que no se puede retornar un Transaction en este caso
                        }
                    }
                    // En caso de error inesperado, retornamos 500 Internal Server Error con Transaction vacío
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null));  // Null para indicar que no se pudo completar la transacción
                });
    }

    // Endpoint para realizar una transferencia entre dos cuentas
    @PostMapping("/transferencia")
    public Mono<Transaction> registerTransfer(@RequestParam String sourceAccount, @RequestParam String destinationAccount, @RequestParam double amount) {
        return transactionService.registerTransfer(sourceAccount, destinationAccount, amount);
    }


    @GetMapping("/historial")
    public Flux<Transaction> viewTransactions(@RequestHeader Map<String, String> headers) {
        return transactionService.listTransactions();
    }

}
