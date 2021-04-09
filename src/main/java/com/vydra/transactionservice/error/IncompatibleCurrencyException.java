package com.vydra.transactionservice.error;

import static java.lang.String.format;

public class IncompatibleCurrencyException extends AccountException {

    public IncompatibleCurrencyException(final String msg, final Object...params) {
        super(format(msg, params));
    }
}
