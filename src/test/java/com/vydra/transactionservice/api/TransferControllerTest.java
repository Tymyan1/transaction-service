package com.vydra.transactionservice.api;

import com.vydra.transactionservice.api.model.TransactionDTO;
import com.vydra.transactionservice.error.AccountException;
import com.vydra.transactionservice.persistence.model.Transaction;
import com.vydra.transactionservice.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class TransferControllerTest {

    private static final long SOURCE = 1L;
    private static final long TARGET = 2L;
    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final Currency CURRENCY = Currency.getInstance("GBP");

    private TransferController controller;

    private ResponseEntity actResult;
    private Exception actThrown;

    private TransactionDTO request;
    private Transaction transaction;

    @Mock
    private TransferService service;

    @BeforeEach
    void setup () {
        controller = new TransferController(service);
    }

    @Test
    void shouldReturnOkStatusWhenServiceReturnsResult() throws AccountException {
        givenARequest();
        givenTheServiceReturnsValidResult();
        whenTransferIsDone();
        thenResultIsAsExpected();
    }

    @Test
    void shouldThrowErrorWhenServiceThrowsAnError() throws AccountException {
        givenARequest();
        givenTheServiceThrows(RuntimeException.class);
        whenTransferAttempted();
        thenExceptionIsThrown(RuntimeException.class);
    }

    private void givenARequest() {
        request = new TransactionDTO(SOURCE, TARGET, AMOUNT);
    }

    private void givenTheServiceReturnsValidResult() throws AccountException {
        transaction = new Transaction(request.getSource(), request.getTarget(), request.getAmount(), CURRENCY);
        when(service.transfer(request.getSource(), request.getTarget(), request.getAmount())).thenReturn(transaction);
    }

    private void givenTheServiceThrows(final Class<? extends Exception> exc) throws AccountException {
        when(service.transfer(anyLong(), anyLong(), any())).thenThrow(exc);
    }

    private void whenTransferAttempted() {
        try {
            whenTransferIsDone();
        } catch (Exception e) {
            actThrown = e;
        }
    }

    private void whenTransferIsDone() throws AccountException {
        actResult = controller.transfer(request);
    }

    private void thenResultIsAsExpected() {
        assertThat(actResult).isEqualTo(ResponseEntity.ok().build());
    }

    private void thenExceptionIsThrown(final Class<? extends Exception> exc) {
        assertThat(actThrown).isInstanceOf(exc);
    }
}