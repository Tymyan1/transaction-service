package com.vydra.featuretest.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vydra.featuretest.steps.states.TransactionContextState;
import com.vydra.transactionservice.api.model.TransactionDTO;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TransactionSteps {

    private static final String TRANSFER_PATH = "/v1/transfer";

    @Autowired
    private TransactionContextState state;
    @Autowired
    private RestTemplate serviceRest;

    private ResponseEntity response;

    @Given("the transaction amount is negative")
    public void negativeTransactionRequest() {
        state.setAmount(BigDecimal.valueOf(-10));
    }

    @Given("a transaction request from {word} to {word}")
    public void aTransactionRequest(final String source, final String target) {
        state.setSource(source);
        state.setTarget(target);
    }

    @When("a transaction request is received")
    public void postTransaction() {
        response = serviceRest.postForEntity(TRANSFER_PATH, buildTransaction(), String.class);
    }

    @Then("the client of the API should receive an error")
    public void clientGotError() {
        assertThat(response.getStatusCode().isError()).isTrue();
    }

    private TransactionDTO buildTransaction() {
        return new TransactionDTO(state.getAccountId(state.getSource()),
                state.getAccountId(state.getTarget()), state.getAmount());
    }

}
