package com.finledger.transaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionHistoryResponse {
    private Long transactionId;
    private Long counterPartyId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime createdDt;
}
