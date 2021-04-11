package com.vydra.featuretest.springconfig;

import com.vydra.featuretest.steps.states.TransactionContextState;
import com.vydra.featuretest.utils.NoErrorRestTemplateErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfiguration {

    @Bean
    public RestTemplate serviceRest() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder
                .rootUri("http://localhost:8080")
                .errorHandler(new NoErrorRestTemplateErrorHandler())
                .build();
    }

    @Bean
    public TransactionContextState transactionContextState() {
        return new TransactionContextState();
    }
}
