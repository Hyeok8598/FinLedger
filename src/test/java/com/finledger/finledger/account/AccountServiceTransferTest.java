package com.finledger.finledger.account;

import static org.assertj.core.api.Assertions.*;
import com.finledger.finledger.support.BaseIntegrationTest;
import com.finledger.finledger.transfer.Transaction;
import com.finledger.finledger.transfer.TransactionRepository;
import com.finledger.finledger.transfer.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class AccountServiceTransferTest extends BaseIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @DisplayName("계좌 이체가 정상적으로 수행된다")
    void transfer_usecase_success() {
        Long fromID = accountService.createAccount("출금고객", new BigDecimal("10000"));
        Long toID   = accountService.createAccount("입금고객", new BigDecimal("5000"));

        accountService.transfer(fromID, toID, new BigDecimal("3000"));

        BigDecimal fromBalance = accountService.getBalance(fromID);
        BigDecimal toBalance   = accountService.getBalance(toID);

        assertThat(fromBalance).isEqualByComparingTo("7000");
        assertThat(toBalance).isEqualByComparingTo("8000");
    }

    @Test
    @DisplayName("동일한 계좌로는 이체할 수 없다")
    void transfer_usecase_fail_when_same_account() {
        Long accountId = accountService.createAccount("고객", new BigDecimal("3000"));

        assertThatThrownBy(() ->
                accountService.transfer(accountId, accountId, new BigDecimal("3000"))
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("동일 계좌로 이체할 수 없습니다.");
    }

    @Test
    @DisplayName("잔액이 부족하면 이체에 실패한다")
    void transfer_usecase_fail_when_insufficient_balance() {
        Long fromID = accountService.createAccount("출금고객", new BigDecimal("2000"));
        Long toID   = accountService.createAccount("입금고객", new BigDecimal("5000"));

        assertThatThrownBy(() ->
                accountService.transfer(fromID, toID, new BigDecimal("3000"))
        ).isInstanceOf(IllegalStateException.class).hasMessage("잔액 부족");
    }

    @Test
    @DisplayName("이체 금액은 0보다 커야 한다")
    void transfer_usecase_fail_when_amount_is_zero_or_negative () {
        Long fromID = accountService.createAccount("출금고객", new BigDecimal("2000"));
        Long toID   = accountService.createAccount("입금고객", new BigDecimal("5000"));

        assertThatThrownBy(() ->
                accountService.transfer(fromID, toID, BigDecimal.ZERO)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("이체 금액은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("이체하면 거래내역이 2건 생성된다")
    void create_usecase_two_transaction_when_transfer() {
        Long fromId = accountService.createAccount("출금고객", new BigDecimal("1000"));
        Long toId   = accountService.createAccount("입금고객", new BigDecimal("5000"));

        accountService.transfer(fromId, toId, new BigDecimal("500"));
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(2);
    }
}
