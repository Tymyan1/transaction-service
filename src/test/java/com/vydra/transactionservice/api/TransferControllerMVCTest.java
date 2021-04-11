package com.vydra.transactionservice.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vydra.transactionservice.api.model.TransactionDTO;
import com.vydra.transactionservice.api.model.TransactionResponse;
import com.vydra.transactionservice.api.model.converters.TransactionConverter;
import com.vydra.transactionservice.error.AccountException;
import com.vydra.transactionservice.error.AccountNotFoundException;
import com.vydra.transactionservice.persistence.model.Transaction;
import com.vydra.transactionservice.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerMVCTest {

    private static final long SOURCE = 1L;
    private static final long TARGET = 2L;
    private static final BigDecimal VALID_AMOUNT = BigDecimal.TEN;
    private static final Currency CURRENCY = Currency.getInstance("GBP");
    private static final Long ID = 1L;

    private static final String TRANSFER_PATH = "/v1/transfer";

    @Autowired
    private TransferController controller;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    private TransactionConverter converter = new TransactionConverter();

    private TransactionDTO request;
    private ResultActions resultActions;
    private MvcResult result;

    @MockBean
    private TransferService service;

    @Test
    void shouldReturn200AndTransactionDetailsWhenServiceSucceeds() throws Exception {
        givenAValidRequest();
        givenTheServiceReturnsAValidResult();
        whenPostTransfer();
        thenStatusIs(200);
        thenResultBodyIsAsExpected();
    }

    private static Stream<Arguments> exceptionsToStatuses() {
        return Stream.of(
                Arguments.of(AccountNotFoundException.class, 404),
                Arguments.of(AccountException.class, 400),
                Arguments.of(IllegalArgumentException.class, 400),
                Arguments.of(RuntimeException.class, 500));
    }

    @ParameterizedTest
    @MethodSource("exceptionsToStatuses")
    void shouldReturnErrorWhenServiceThrowsException(final Class<? extends Exception> exc, final int status)
            throws Exception {
        givenAValidRequest();
        givenTheServiceThrows(exc);
        whenPostTransfer();
        thenStatusIs(status);
    }

    private void givenAValidRequest() {
        request = new TransactionDTO(SOURCE, TARGET, VALID_AMOUNT);
    }

    private void givenTheServiceReturnsAValidResult() throws AccountException {
        Transaction trans = validTransaction();
        when(service.transfer(request.getSource(), request.getTarget(), request.getAmount()))
                .thenReturn(trans);
    }

    private void givenTheServiceThrows(final Class<? extends Exception> exc) throws AccountException {
        when(service.transfer(anyLong(), anyLong(), any())).thenThrow(exc);
    }

    private void whenPostTransfer() throws Exception {
        resultActions = mvc.perform(post(TRANSFER_PATH)
                .content(mapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private void thenStatusIs(final int status) throws Exception {
        resultActions = resultActions.andExpect(status().is(status));
    }

    private void thenResultBodyIsAsExpected() throws UnsupportedEncodingException, JsonProcessingException {
        result = resultActions.andReturn();
        TransactionResponse response = mapper.readValue(result.getResponse().getContentAsString(),
                TransactionResponse.class);
        assertThat(response).isEqualTo(converter.convert(validTransaction()));
    }

    private Transaction validTransaction() {
        Transaction trans = spy(new Transaction(SOURCE, TARGET, VALID_AMOUNT, CURRENCY));
        when(trans.getId()).thenReturn(ID);
        return trans;
    }

}