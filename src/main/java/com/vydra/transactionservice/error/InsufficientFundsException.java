package com.vydra.transactionservice.error;

import static java.lang.String.format;

public class InsufficientFundsException extends AccountException {

    public InsufficientFundsException(final String msg, final Object...params) {
        super(format(msg, params));
    }
}
