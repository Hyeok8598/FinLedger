package com.finledger.finledger.account;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Transactional
    public Long createAccount(String custNm, BigDecimal balance) {
        Account account = new Account(custNm, balance);
        Account saved   = this.accountRepository.save(account);
        String accountNumber = accountNumberGenerator.generate(saved.getId());
        saved.assignAccountNumber(accountNumber);

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

    @Transactional
    public String getAccountNumber(Long AccountId) {
        return this.getAccount(AccountId).getAccountNumber();
    }

    private Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("계좌가 존재하지 않습니다."));
    }
}