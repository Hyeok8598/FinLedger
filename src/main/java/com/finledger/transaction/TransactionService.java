package com.finledger.transaction;

import com.finledger.account.AccountRepository;
import com.finledger.account.AccountService;
import com.finledger.account.exception.AccountErrorLabel;
import com.finledger.account.exception.AccountException;
import com.finledger.transaction.api.dto.TransactionHistoryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void recordTransfer(Long fromtoAccountId, Long toAccountId, BigDecimal amount) {
        Transaction withdrawHistory = new Transaction(fromtoAccountId, toAccountId, amount, "W");
        transactionRepository.save(withdrawHistory);
        Transaction depositHistory  = new Transaction(toAccountId, fromtoAccountId, amount, "D");
        transactionRepository.save(depositHistory);
    }

    @Transactional
    public List<TransactionHistoryResponse> getTransaction(Long accountId) {
        if(accountRepository.findByAccountId(accountId).isEmpty()) {
            throw new AccountException(AccountErrorLabel.ACCOUNT_NOT_FOUNT);
        }

        List<Transaction> transactions =
                transactionRepository.findByAccountIdOrderByCreatedDtDesc(accountId);

        return transactions.stream()
                .map(t -> new TransactionHistoryResponse(
                        t.getId(),
                        t.getCounterPartyId(),
                        t.getAmount(),
                        t.getType(),
                        t.getCreatedDt()
                )).toList();
    }
}
