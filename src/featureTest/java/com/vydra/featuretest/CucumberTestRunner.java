package com.vydra.featuretest;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/featureTest/resources/features"
)
public class CucumberTestRunner {

}