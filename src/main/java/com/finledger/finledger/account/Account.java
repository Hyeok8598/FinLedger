package com.finledger.finledger.account;

import com.finledger.finledger.account.exception.AccountErrorLabel;
import com.finledger.finledger.account.exception.AccountException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 추후 개발 예정(채번로직) */
    @Column(nullable = true, unique = true, length = 32)
    private String accountNumber;

    @Column(nullable = false, length = 100)
    private String custNm;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(nullable = false)
    private LocalDateTime updatedDt;

    public Account(String custNm, BigDecimal balance) {
        validateCustNm(custNm);
        validateInitAmount(balance);

        this.custNm = custNm;
        this.balance = balance;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDt = now;
        this.updatedDt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDt = LocalDateTime.now();
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);

        if(this.balance.compareTo(amount) < 0) {
            throw new AccountException(AccountErrorLabel.INSUFFICIENT_BALANCE);
        }

        this.balance = this.balance.subtract(amount);
    }

    public void assignAccountNumber(String accountNumber) {
        if(this.accountNumber != null) {
            throw new AccountException(AccountErrorLabel.ACCOUNT_NUMBER_ALREADY_ASSIGNED);
        }

        if(accountNumber == null || accountNumber.isBlank()) {
            throw new AccountException(AccountErrorLabel.ACCOUNT_NUMBER_REQUIRED);
        }

        this.accountNumber = accountNumber;
    }

    private void validateCustNm(String custNm) {
        if(custNm == null || custNm.isBlank()) {
            throw new AccountException(AccountErrorLabel.CUSTOMER_NAME_REQUIRED);
        }
    }

    private void validateAmount(BigDecimal amount) {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountException(AccountErrorLabel.INVALID_AMOUNT);
        }
    }

    private void validateInitAmount(BigDecimal amount) {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountException(AccountErrorLabel.INVALID_AMOUNT);
        }
    }
}
