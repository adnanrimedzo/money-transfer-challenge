package com.dws.challenge.exception;

public class AccountNotExist extends RuntimeException {
    public AccountNotExist(String message) {
        super(message);
    }
}
