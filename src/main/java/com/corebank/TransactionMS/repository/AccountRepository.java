package com.corebank.TransactionMS.repository;

import com.corebank.TransactionMS.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String>{
    Mono<Account> findByAccountNumber(String accountNumber);
}