package com.finledger.finledger.account;

import com.finledger.finledger.support.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

public class AccountServiceTest extends BaseIntegrationTest {

    @Autowired
    AccountService accountService;
    AccountRepository accountRepository;

    @Test
    void deposit_usecase_increases_balance() {
        accountService.createAccount("아무개", new BigDecimal("0"));;

//        accountService.deposit();
    }
}
