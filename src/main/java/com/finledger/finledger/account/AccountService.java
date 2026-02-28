package com.finledger.finledger.account;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // id 채번 로직을 넣어야함.
    @Transactional
    public void createAccount(String custNm, BigDecimal balance) {
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