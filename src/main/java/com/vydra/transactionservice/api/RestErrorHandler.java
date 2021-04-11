package com.vydra.transactionservice.api;

import com.vydra.transactionservice.error.AccountException;
import com.vydra.transactionservice.error.AccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.MediaType.TEXT_PLAIN;

@RestControllerAdvice
public class RestErrorHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity notFound(final AccountNotFoundException e) {
        return fromException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AccountException.class, IllegalArgumentException.class})
    public ResponseEntity badRequest(final Exception e) {
        return fromException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity internalError(final Exception e) {
        return fromException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity fromException(final Exception e, final HttpStatus status) {
        return ResponseEntity.status(status)
                .contentType(TEXT_PLAIN)
                .body(e.getMessage());
    }
}
