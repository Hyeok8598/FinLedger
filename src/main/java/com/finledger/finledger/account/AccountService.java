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

    @Transactional
    public Long createAccount(String custNm, BigDecimal balance) {
        Account account = new Account(custNm, balance);
        Account saved   = this.accountRepository.save(account);
        return saved.getId();
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
        account.deposit(amount);

        return account;
    }

    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = getAccount(accountId);
        account.withdraw(amount);
    }

    @Transactional
    public BigDecimal getBalance(Long accountId) {
        return this.getAccount(accountId).getBalance();
    }

    private Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));
    }
}