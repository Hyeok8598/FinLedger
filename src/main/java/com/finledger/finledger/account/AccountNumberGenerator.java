package com.finledger.finledger.account;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class AccountNumberGenerator {
    public String generate(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("계좌번호 생성용 ID는 필수입니다.");
        }

        /* ID 값 생성 후, ACC + yyyyMMdd + id(000000) */

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String idPart = String.format("%06d", id);

        return "ACC" + date + idPart;
    }
}
