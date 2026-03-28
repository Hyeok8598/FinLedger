package com.finledger.account.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CreateAccountRequest {

    private String ownerName;
    private BigDecimal initialBalance;
}
