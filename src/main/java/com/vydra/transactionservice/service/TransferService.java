package com.vydra.transactionservice.service;

import com.vydra.transactionservice.error.AccountException;
import com.vydra.transactionservice.error.IncompatibleCurrencyException;
import com.vydra.transactionservice.error.InsufficientFundsException;
import com.vydra.transactionservice.persistence.model.Account;
import com.vydra.transactionservice.persistence.model.Transaction;
import com.vydra.transactionservice.persistence.repositories.AccountRepository;
import com.vydra.transactionservice.persistence.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class TransferService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class )
    public Transaction transfer(final long sourceId, final long targetId, final BigDecimal amount)
            throws AccountException {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(format("Amount to transfer must be > 0, was %s", amount));
        }

        if (sourceId == targetId) {
            throw new IllegalArgumentException("Cannot transfer money from and to the same account");
        }

        final Account source = accountRepository.findById(sourceId)
                .orElseThrow(() -> new AccountException("Account '%s' not found", sourceId));
        final Account target = accountRepository.findById(targetId)
                .orElseThrow(() -> new AccountException("Account '%s' not found", targetId));

        if (source.getCurrency() != target.getCurrency()) {
            //TODO handle different currencies?
            throw new IncompatibleCurrencyException("Attempted to transfer using incompatible currencies (%s -> %s)",
                    source.getCurrency().getCurrencyCode(),
                    target.getCurrency().getCurrencyCode());
        }

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Account does not have enough balance to transfer %s %s",
                    source.getBalance(), source.getCurrency().getCurrencyCode());
        }

        return doTransfer(source, target, amount);
    }

    private Transaction doTransfer(final Account source, final Account target, final BigDecimal amount) {
        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(target);
        return transactionRepository.save(
                new Transaction(source.getId(), target.getId(), amount, source.getCurrency()));
    }
}
