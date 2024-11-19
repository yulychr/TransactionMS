package com.corebank.TransactionMS.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document (collection = "account")
public class Account {
    @BsonId
    private String id;             // ID de la cuenta, este es el identificador de la base de datos
    private String accountNumber;   // Número de cuenta (clave de búsqueda)
    private double balance;
    private String typeAccount;
    private String customerId;
}
