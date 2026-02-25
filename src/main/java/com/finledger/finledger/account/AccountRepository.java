package com.finledger.finledger.account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(Long id);
    Account save(Account account);
}
