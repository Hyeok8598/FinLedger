package com.finledger.finledger.account.exception;

public enum AccountErrorLabel {
    INVALID_AMOUNT("금액은 0보다 커야 합니다."),
    INSUFFICIENT_BALANCE("잔액이 부족합니다."),
    CUSTOMER_NAME_REQUIRED("고객명은 필수입니다."),
    ACCOUNT_NUMBER_ALREADY_ASSIGNED("계좌번호는 이미 할당되었습니다."),
    ACCOUNT_NUMBER_REQUIRED("계좌번호는 필수입니다."),
    SAME_ACCOUNT_TRANSFER("동일한 계좌로 이체할 수 없습니다."),
    ACCOUNT_NOT_FOUNT("계좌를 찾을 수 없습니다."),
    ACCOUNT_ID_REQUIRED("계좌번호 생성을 위한 계좌 ID는 필수입니다.");

    private final String message;

    AccountErrorLabel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
