package com.vydra.transactionservice.error;

import static java.lang.String.format;

public class AccountException extends Exception {
    public AccountException(final String msg, final Object...params) {
        super(format(msg, params));
    }
}
