package com.finledger.finledger.account;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void CreateAccount(String custNm, BigDecimal balance) {
        Account account = new Account(custNm, balance);
    }

    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
        account.withdraw(amount);
    }

    private Account getAccount(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));
    }
}
