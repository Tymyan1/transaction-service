package com.vydra.transactionservice.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PingControllerMVCTest {

    private static final String PING_PATH = "/ping";

    @Autowired
    private PingController controller;

    @Autowired
    private MockMvc mvc;

    private ResultActions actResult;

    @Test
    void shouldReturnPongAnd200OnPing() throws Exception {
        whenGetPingCalled();
        thenStatusIs(200);
        thenBodyContains("pong");
    }

    private void whenGetPingCalled() throws Exception {
        actResult = mvc.perform(get(PING_PATH));
    }

    private void thenStatusIs(final int status) throws Exception {
        actResult = actResult.andExpect(status().is(status));
    }

    private void thenBodyContains(final String body) throws UnsupportedEncodingException {
        assertThat(actResult.andReturn().getResponse().getContentAsString()).isEqualTo(body);
    }

}