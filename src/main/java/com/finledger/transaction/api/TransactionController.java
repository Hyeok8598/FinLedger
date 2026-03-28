package com.finledger.transaction.api;

import com.finledger.common.api.ApiResponse;
import com.finledger.transaction.Transaction;
import com.finledger.transaction.TransactionService;
import com.finledger.transaction.api.dto.TransactionHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private TransactionService transactionService;

    @GetMapping("/api/accounts/{accountId}/transactions")
    public ApiResponse<List<TransactionHistoryResponse>> getTransactions(@PathVariable Long accountId) {
        return ApiResponse.success(transactionService.getTransaction(accountId));
    }
}
