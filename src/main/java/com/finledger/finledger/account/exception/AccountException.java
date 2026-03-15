package com.finledger.finledger.account.exception;

import com.finledger.finledger.common.exception.BusinessException;

public class AccountException extends BusinessException {

    private final AccountErrorLabel label;

    public AccountException(AccountErrorLabel label) {
        super(label, label.getMessage());
        this.label = label;
    }

    public AccountErrorLabel getAccountErrorLabel() {
        return this.label;
    }
}
