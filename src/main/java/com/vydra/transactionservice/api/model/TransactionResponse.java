package com.vydra.transactionservice.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
public class TransactionResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("source")
    private Long sourceAccountId;
    @JsonProperty("target")
    private Long targetAccountId;
    @JsonProperty("amount")
    private BigDecimal amount;
    @JsonProperty("currency")
    private Currency currency;
}
