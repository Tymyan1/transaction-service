package com.vydra.transactionservice.api.model.converters;

import com.vydra.transactionservice.api.model.TransactionResponse;
import com.vydra.transactionservice.persistence.model.Transaction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter implements Converter<Transaction, TransactionResponse> {

    @Override
    public TransactionResponse convert(final Transaction source) {
        return new TransactionResponse(source.getId(), source.getSourceAccountId(), source.getTargetAccountId(),
                source.getAmount(), source.getCurrency());
    }
}
