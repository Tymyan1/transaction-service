package com.vydra.transactionservice.api;

import com.vydra.transactionservice.api.model.TransactionDTO;
import com.vydra.transactionservice.api.model.converters.TransactionConverter;
import com.vydra.transactionservice.error.AccountException;
import com.vydra.transactionservice.persistence.model.Transaction;
import com.vydra.transactionservice.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/v1")
@AllArgsConstructor
public class TransferController {

    private TransferService service;
    private TransactionConverter converter;

    @RequestMapping(value = "/transfer", method = POST)
    public ResponseEntity transfer(@RequestBody final TransactionDTO transaction) throws AccountException {
        Transaction result = service.transfer(transaction.getSource(), transaction.getTarget(),
                transaction.getAmount());

        return ResponseEntity.ok(converter.convert(result));
    }
}
