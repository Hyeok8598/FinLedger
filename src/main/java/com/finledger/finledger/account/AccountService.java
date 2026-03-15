package com.finledger.finledger.account;

import com.finledger.finledger.account.exception.AccountErrorLabel;
import com.finledger.finledger.account.exception.AccountException;
import com.finledger.finledger.transaction.TransactionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.transactionService = transactionService;

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
    public String getAccountNumber(Long accountId) {
        return this.getAccount(accountId).getAccountNumber();
    }

    @Transactional
    public BigDecimal getBalance(String accountNumber) {
        return this.getAccountByAccountNumber(accountNumber).getBalance();
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        validateTransferRequest(fromId, toId, amount);

        /* Dead Lock 방지 */
        Long firstID  = Math.min(fromId, toId);
        Long secondID = Math.max(fromId, toId);

        Account firstAccount  = getAccountForUpdate(firstID);
        Account secondAccount = getAccountForUpdate(secondID);

        Account fromAccount = firstAccount.getId().equals(fromId) ? firstAccount : secondAccount;
        Account toAccount   = firstAccount.getId().equals(toId) ? firstAccount : secondAccount;

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        transactionService.recordTransfer(fromId, toId, amount);
    }

    private Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(AccountErrorLabel.ACCOUNT_NOT_FOUNT));
    }

    private Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(AccountErrorLabel.ACCOUNT_NOT_FOUNT));
    }

    private Account getAccountForUpdate(Long id) {
        return accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new AccountException(AccountErrorLabel.ACCOUNT_NOT_FOUNT));
    }

    private void validateTransferRequest(Long fromId, Long toId, BigDecimal amount) {
        if(fromId == null || toId == null) {
            throw new AccountException(AccountErrorLabel.ACCOUNT_NOT_FOUNT);
        }

        if(fromId.equals(toId)) {
            throw new AccountException(AccountErrorLabel.SAME_ACCOUNT_TRANSFER);
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountException(AccountErrorLabel.INVALID_AMOUNT);
        }
    }
}