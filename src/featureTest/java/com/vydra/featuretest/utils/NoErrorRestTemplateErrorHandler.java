package com.vydra.featuretest.utils;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class NoErrorRestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return false;
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        // will never be an error
    }
}
