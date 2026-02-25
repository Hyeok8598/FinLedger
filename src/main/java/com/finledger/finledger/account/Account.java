package com.finledger.finledger.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        validateAmount(balance);

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
            throw new IllegalStateException("잔액 부족");
        }

        this.balance = this.balance.subtract(amount);
    }

    private void validateCustNm(String custNm) {
        if(custNm == null || custNm.isBlank()) {
            throw new IllegalArgumentException("고객명은 필수입니다.");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }
}
