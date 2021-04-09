package com.vydra.transactionservice.persistence.repositories;

import com.vydra.transactionservice.persistence.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class TransactionRepositoryJPATest {

    private static final Long SOURCE = 1L;
    private static final Long TARGET = 1L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(12.34);
    private static final Currency CURRENCY = Currency.getInstance("GBP");

    private Transaction transaction;
    private Transaction actResult;

    @Autowired
    private TransactionRepository repository;

    @Test
    void shouldFindSavedTransactionById() {
        givenATransaction();
        whenTransactionIsSaved();
        thenFindByResultIdFindsTheSameTransaction();
    }

    private void givenATransaction() {
        transaction = new Transaction(SOURCE, TARGET, AMOUNT, CURRENCY);
    }

    private void whenTransactionIsSaved() {
        actResult = repository.save(transaction);
    }

    private void thenFindByResultIdFindsTheSameTransaction() {
        assertThat(repository.findById(actResult.getId())).contains(actResult);
    }

}