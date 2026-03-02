package com.finledger.finledger.account;

import com.finledger.finledger.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

public class AccountServiceTest extends BaseIntegrationTest {

    @Autowired
    AccountService accountService;

    @Test
    void deposit_usecase_increases_balance() {
        Long accountId = accountService.createAccount("아무개", new BigDecimal("0"));;

        accountService.deposit(accountId, new BigDecimal("500"));

        // DB에 저장된 Account 최종 조회
        BigDecimal balance = accountService.getBalance(accountId);
        assertThat(balance).isEqualByComparingTo("500");
    }

    @Test
    void withdraw_usecase_decrease_balacne() {
        Long accountId = accountService.createAccount("김아무개", new BigDecimal(0));

        accountService.deposit(accountId, new BigDecimal("500"));
        accountService.withdraw(accountId, new BigDecimal("500"));

        // DB에 저장된 Account 최종 조회
        BigDecimal balance = accountService.getBalance(accountId);

        assertThat(balance).isEqualByComparingTo("0");
    }
}