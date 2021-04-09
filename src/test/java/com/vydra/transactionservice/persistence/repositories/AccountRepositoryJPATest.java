package com.vydra.transactionservice.persistence.repositories;

import com.vydra.transactionservice.persistence.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AccountRepositoryJPATest {

    private static final Currency CURRENCY = Currency.getInstance("GBP");
    private static final BigDecimal BALANCE = BigDecimal.valueOf(12.34);

    private Account account;
    private Account actResult;

    @Autowired
    private AccountRepository repository;

    @Test
    void shouldHaveTheSameBalanceAndCurrencyOnSave() {
        givenAnAccount();
        whenAccountIsSaved();
        thenTheSavedAccountIsAsExpected();
    }

    @Test
    void shouldFindSavedAccountById() {
        givenAnAccount();
        whenAccountIsSaved();
        thenFindByResultIdFindsTheSameAccount();
    }

    private void givenAnAccount() {
        account = new Account(BALANCE, CURRENCY);
    }

    private void whenAccountIsSaved() {
        actResult = repository.save(account);
    }

    private void thenFindByResultIdFindsTheSameAccount() {
        assertThat(repository.findById(actResult.getId())).contains(actResult);
    }

    private void thenTheSavedAccountIsAsExpected() {
        assertThat(actResult).isEqualTo(account);
    }
}