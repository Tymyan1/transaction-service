package com.vydra.featuretest.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PingSteps {

    @Autowired
    private RestTemplate serviceRest;
    @Autowired
    private ApplicationContext context;

    private ResponseEntity<String> response;



    @When("I ping the service")
    public void ping() {
        response = serviceRest.exchange("/ping", HttpMethod.GET, null, String.class);
    }

    @Then("I get response status of {int}")
    public void responseStatus(final int status) {
        assertThat(response.getStatusCodeValue()).isEqualTo(status);
    }

    @Then("the response body is {string}")
    public void responseBody(final String body) {
        assertThat(response.getBody()).isEqualTo(body);
    }


}
