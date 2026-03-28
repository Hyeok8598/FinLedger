package com.finledger.transaction;

import com.finledger.account.AccountService;
import com.finledger.account.exception.AccountErrorLabel;
import com.finledger.account.exception.AccountException;
import com.finledger.support.BaseIntegrationTest;
import com.finledger.transaction.api.dto.TransactionHistoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TransactionServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("특정 계좌의 거래 내역을 최신순으로 조회한다")
    void get_transactions_returns_history_in_desc_order() {
        Long fromAccountId = accountService.createAccount("출금고객", new BigDecimal("10000"));
        Long toAccountIdA  = accountService.createAccount("입금고객A", new BigDecimal("0"));
        Long toAccountIdB  = accountService.createAccount("입금고객B", new BigDecimal("0"));

        transactionService.recordTransfer(fromAccountId, toAccountIdA, new BigDecimal("1000"));
        transactionService.recordTransfer(fromAccountId, toAccountIdB, new BigDecimal("2000"));

        List<TransactionHistoryResponse> histories = transactionService.getTransaction(fromAccountId);

        assertThat(histories).hasSize(2);

        assertThat(histories.get(0).getCounterPartyId()).isEqualTo(toAccountIdB);
        assertThat(histories.get(0).getAmount()).isEqualByComparingTo("2000");
        assertThat(histories.get(0).getType()).isEqualTo("W");

        assertThat(histories.get(1).getCounterPartyId()).isEqualTo(toAccountIdA);
        assertThat(histories.get(1).getAmount()).isEqualByComparingTo("1000");
        assertThat(histories.get(1).getType()).isEqualTo("W");
    }

    @Test
    @DisplayName("존재하지 않는 계좌의 거래 내역은 조회할 수 없다")
    void get_transaction_throws_exception_when_account_not_found() {
        Long notExistAccountId = 9999L;

        assertThatThrownBy(() ->
                transactionService.getTransaction(notExistAccountId))
                .isInstanceOf(AccountException.class)
                .satisfies(ex -> {
                    AccountException ae = (AccountException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo(AccountErrorLabel.ACCOUNT_NOT_FOUNT);
                });
    }
}
