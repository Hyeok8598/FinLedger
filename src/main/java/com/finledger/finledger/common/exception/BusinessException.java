package com.finledger.finledger.common.exception;

public class BusinessException extends RuntimeException{

    private final Enum<?> errorCode;

    public BusinessException(Enum<?> errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Enum<?> getErrorCode() {
        return errorCode;
    }
}
