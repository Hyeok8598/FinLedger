package com.finledger.finledger.transfer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    private Long accountId;
    private Long counterPartyId;
    private BigDecimal amount;

    /* type : W:withdrar, D:deposit */
    private String type;
    private LocalDateTime createdDt;

    public Transaction(Long accountId, Long counterPartyId, BigDecimal amount, String type) {
        this.accountId = accountId;
        this.counterPartyId = counterPartyId;
        this.amount = amount;
        this.type = type;
        this.createdDt = LocalDateTime.now();
    }
}
