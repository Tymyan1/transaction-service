package com.vydra.featuretest.steps.states;

import io.cucumber.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Slf4j
public class StateSteps {

    @Autowired
    private List<State> states;
    @Autowired
    private List<CrudRepository> repositories;

    @Given("a refreshed state")
    public void refreshState() {
        log.info("State refreshed");
        states.forEach(State::refresh);
        repositories.forEach(CrudRepository::deleteAll);
    }

}
