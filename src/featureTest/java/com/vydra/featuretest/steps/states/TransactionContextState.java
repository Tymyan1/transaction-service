package com.vydra.featuretest.steps.states;

import com.vydra.transactionservice.persistence.model.Account;
import io.cucumber.java.en.Given;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Component
public class TransactionContextState implements State {

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.TEN;

    private Map<String, Account> accounts = new HashMap<>();
    private String source;
    private String target;
    private BigDecimal amount;

    public Long getAccountId(final String type) {
        //TODO better null handling?
        return Optional.ofNullable(accounts.get(type)).map(Account::getId).orElse(null);
    }

    public void refresh() {
        accounts = new HashMap<>();
        source = null;
        target = null;
        amount = DEFAULT_AMOUNT;
    }

}
