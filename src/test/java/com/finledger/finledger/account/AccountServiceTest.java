package com.finledger.finledger.account;

import com.finledger.finledger.account.exception.AccountErrorLabel;
import com.finledger.finledger.account.exception.AccountException;
import com.finledger.finledger.support.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

public class AccountServiceTest extends BaseIntegrationTest {

    @Autowired
    AccountService accountService;

    @DisplayName("입금 시 잔액이 증가한다")
    @Test
    void deposit_usecase_increases_balance() {
        Long accountId = accountService.createAccount("아무개", BigDecimal.ZERO);;

        accountService.deposit(accountId, new BigDecimal("500"));

        // DB에 저장된 Account 최종 조회
        BigDecimal balance = accountService.getBalance(accountId);
        assertThat(balance).isEqualByComparingTo("500");
    }

    @DisplayName("출금 시 잔액이 감소한다")
    @Test
    void withdraw_usecase_decrease_balacne() {
        Long accountId = accountService.createAccount("김아무개", BigDecimal.ZERO);

        accountService.deposit(accountId, new BigDecimal("500"));
        accountService.withdraw(accountId, new BigDecimal("500"));

        // DB에 저장된 Account 최종 조회
        BigDecimal balance = accountService.getBalance(accountId);

        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @DisplayName("잔액보다 큰 금액을 출금하면 예외가 발생한다")
    @Test
    void withdraw_usecase_throws_exception_when_balance_insufiicient() {
        Long accountId = accountService.createAccount("박아무개", new BigDecimal("1000"));

        assertThatThrownBy(() ->
                accountService.withdraw(accountId, new BigDecimal("2000"))
        )
                .isInstanceOf(AccountException.class)
                .satisfies(ex ->{
                   AccountException ae = (AccountException) ex;
                   assertThat(ae.getErrorCode()).isEqualTo(AccountErrorLabel.INSUFFICIENT_BALANCE);
                });
    }

    @DisplayName("0원 이하 금액은 입금할 수 없다.")
    @Test
    void deposit_usecase_rejects_zero_or_negative_amount() {
        Long accountId = accountService.createAccount("박아무개", new BigDecimal("1000"));

        assertThatThrownBy(()->
                accountService.deposit(accountId, BigDecimal.ZERO)
        )
                .isInstanceOf(AccountException.class)
                .satisfies(ex -> {
                   AccountException ae = (AccountException) ex;
                   assertThat(ae.getErrorCode()).isEqualTo(AccountErrorLabel.INVALID_AMOUNT);
                });
    }

    @DisplayName("존재하지 않는 계좌 조회 시 예외가 발생한다")
    @Test
    void get_balance_throws_exception_when_account_not_found() {
        assertThatThrownBy(() ->
            accountService.getBalance(999L)
        )
                .isInstanceOf(AccountException.class)
                .satisfies(ex -> {
                    AccountException ae = (AccountException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo(AccountErrorLabel.ACCOUNT_NOT_FOUNT);
                });
    }

    @DisplayName("계좌 생성 시, 계좌번호가 자동으로 채번된다")
    @Test
    void create_account_assigns_account_number() {
        Long accountId = accountService.createAccount("임아무개", new BigDecimal("1000"));

        String accountNumber = accountService.getAccountNumber(accountId);

        assertThat(accountNumber).isNotBlank();
        assertThat(accountNumber).startsWith("ACC");
    }

    @DisplayName("계좌번호로 잔액 조회가 가능하다")
    @Test
    void search_usecase_balance_by_accountNumber() {
        Long accountId = accountService.createAccount("나아무개", new BigDecimal("2000"));

        String accountNumber = accountService.getAccountNumber(accountId);
        BigDecimal balance = accountService.getBalance(accountNumber);

        assertThat(balance).isEqualByComparingTo(new BigDecimal("2000"));
    }
}