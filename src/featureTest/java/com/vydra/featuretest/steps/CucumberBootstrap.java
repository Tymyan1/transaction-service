package com.vydra.featuretest.steps;

import com.vydra.featuretest.springconfig.ApiConfiguration;
import com.vydra.transactionservice.TransactionServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {ApiConfiguration.class, TransactionServiceApplication.class})
public class CucumberBootstrap {
}
