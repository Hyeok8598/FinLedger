package com.finledger.finledger.account;

import com.finledger.finledger.account.exception.AccountErrorLabel;
import com.finledger.finledger.account.exception.AccountException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class AccountNumberGenerator {
    public String generate(Long id) {
        if(id == null) {
            throw new AccountException(AccountErrorLabel.ACCOUNT_ID_REQUIRED);
        }

        /* ID 값 생성 후, ACC + yyyyMMdd + id(000000) */

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String idPart = String.format("%06d", id);

        return "ACC" + date + idPart;
    }
}
