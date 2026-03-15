package com.finledger.finledger.transaction;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void recordTransfer(Long fromtoAccountId, Long toAccountId, BigDecimal amount) {
        Transaction withdrawHistory = new Transaction(fromtoAccountId, toAccountId, amount, "W");
        transactionRepository.save(withdrawHistory);
        Transaction depositHistory  = new Transaction(toAccountId, fromtoAccountId, amount, "D");
        transactionRepository.save(depositHistory);
    }
}
