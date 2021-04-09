package com.vydra.transactionservice.api.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.math.BigDecimal;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
class TransactionDTOTest {

    private static final Long SOURCE = 1L;
    private static final Long TARGET = 2L;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(12.34);

    @Autowired
    private JacksonTester<TransactionDTO> json;

    @Test
    void serialization() throws IOException {
        assertThat(json.write(fullObject()).getJson()).isEqualTo(fullContent());
    }


    private TransactionDTO fullObject() {
        return new TransactionDTO(SOURCE, TARGET, AMOUNT);
    }

    private String fullContent() {
        return format("{\"sourceAccount\":%s,\"targetAccount\":%s,\"transferAmount\":%s}", SOURCE, TARGET, AMOUNT);
    }

}