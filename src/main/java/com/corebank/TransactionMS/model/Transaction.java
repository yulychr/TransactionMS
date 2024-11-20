package com.corebank.TransactionMS.model;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document (collection = "transaction")
public class Transaction {
    @BsonId
    private String id;
    private String type;
    private double amount;
    private LocalDateTime date;
    private String sourceAccount;
    private String destinationAccount;
}
