package com.vydra.transactionservice.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class TransactionDTO {

    @JsonProperty("sourceAccount")
    Long source;
    @JsonProperty("targetAccount")
    Long target;
    @JsonProperty("transferAmount")
    BigDecimal amount;
}
