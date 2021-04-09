package com.vydra.transactionservice.error;

public class AccountNotFoundException extends AccountException {

    public AccountNotFoundException(final String msg, final Object... params) {
        super(msg, params);
    }
}
