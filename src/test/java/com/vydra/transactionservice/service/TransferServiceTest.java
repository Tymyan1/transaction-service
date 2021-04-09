package com.vydra.transactionservice.service;

import com.vydra.transactionservice.error.AccountException;
import com.vydra.transactionservice.error.IncompatibleCurrencyException;
import com.vydra.transactionservice.error.InsufficientFundsException;
import com.vydra.transactionservice.persistence.model.Account;
import com.vydra.transactionservice.persistence.model.Transaction;
import com.vydra.transactionservice.persistence.repositories.AccountRepository;
import com.vydra.transactionservice.persistence.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceTest {

    private static final long SOURCE = 1;
    private static final long TARGET = 2;
    private static final Currency CURRENCY = Currency.getInstance("GBP");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency DOLLAR = Currency.getInstance("USD");

    private Account source;
    private Account target;
    @Mock
    private Transaction transaction;

    private Transaction actResult;
    private Exception actThrown;

    private TransferService service;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() {
        givenAService();
    }

    @Test
    void shouldSucceedWithValidTransferValues() {
        givenAnAccount(SOURCE, 5);
        givenAnAccount(TARGET, 10);
        givenATransaction(5);
        givenTheTransactionIsReturnedByRepository();
        whenTheTransferIsDone();

        thenTheResultTransactionIsAsExpected();
        thenTheAccountsHaveBeenUpdatedTo(0, 15);
    }

    @Test
    void shouldThrowExceptionWithNegativeAmountTransfer() {
        givenSomeAccounts();
        givenATransaction(-5);
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(IllegalArgumentException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWithZeroAmountTransfer() {
        givenSomeAccounts();
        givenATransaction(0);
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(IllegalArgumentException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWhenSourceNotFound() {
        givenAccountNotFound(SOURCE);
        givenAnAccount(TARGET, 10);
        givenATransaction();
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(AccountException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWhenTargetNotFound() {
        givenAnAccount(SOURCE, 10);
        givenAccountNotFound(TARGET);
        givenATransaction();
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(AccountException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWhenNeitherAccountFound() {
        givenAccountNotFound(SOURCE);
        givenAccountNotFound(TARGET);
        givenATransaction();
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(AccountException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWhenCurrenciesDontMatch() {
        givenAnAccount(SOURCE, 10, EUR);
        givenAnAccount(TARGET, 10, DOLLAR);
        givenATransaction();
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(IncompatibleCurrencyException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWhenBalanceTooLow() {
        givenAnAccount(SOURCE, 0);
        givenAnAccount(TARGET, 10);
        givenATransaction(10);
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(InsufficientFundsException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    @Test
    void shouldThrowExceptionWhenTransferringToTheSameAccount() {
        givenAnAccount(SOURCE, 0);
        givenATransaction(SOURCE, SOURCE,10);
        whenTheTransferIsAttempted();

        thenExceptionIsThrown(IllegalArgumentException.class);
        thenTheAccountsHaveNotBeenUpdated();
    }

    private void givenAService() {
        service = new TransferService(accountRepository, transactionRepository);
    }

    private void givenTheTransactionIsReturnedByRepository() {
        when(transactionRepository.save(any())).thenReturn(transaction);
    }

    private void givenAnAccount(final long id, final double balance, final Currency currency) {
        Account account = spy(new Account(BigDecimal.valueOf(balance), currency));
        when(accountRepository.findById( id)).thenReturn(Optional.of(account));
        if (id == SOURCE) {
            source = account;
        }
        if (id == TARGET) {
            target = account;
        }
    }

    private void givenAnAccount(final long id, final double balance) {
        givenAnAccount(id, balance, CURRENCY);
    }

    private void givenSomeAccounts() {
        givenAnAccount(SOURCE, 500);
        givenAnAccount(TARGET, 500);
    }

    private void givenAccountNotFound(final long id) {
        when(accountRepository.findById(id)).thenReturn(Optional.empty());
    }

    private void givenATransaction() {
        givenATransaction(500);
    }

    private void givenATransaction(final double amount) {
        givenATransaction(SOURCE, TARGET, amount);
    }

    private void givenATransaction(final long source, final long target, final double amount) {
        transaction = spy(new Transaction(source, target, BigDecimal.valueOf(amount), CURRENCY));
    }

    private void whenTheTransferIsDone() {
        try {
            actResult = service.transfer(transaction.getSourceAccountId(), transaction.getTargetAccountId(),
                    transaction.getAmount());
        } catch (AccountException e) {
            throw new RuntimeException(e);
        }
    }

    private void whenTheTransferIsAttempted() {
        try {
            actResult = service.transfer(transaction.getSourceAccountId(), transaction.getTargetAccountId(),
                    transaction.getAmount());
        } catch (final Exception e) {
            actThrown = e;
        }
    }

    private void thenTheAccountsHaveBeenUpdatedTo(final double sourceNewBalance, final double targetNewBalance) {
        thenAccountHaveBeenUpdated(source, BigDecimal.valueOf(sourceNewBalance));
        thenAccountHaveBeenUpdated(target, BigDecimal.valueOf(targetNewBalance));
    }

    private void thenTheAccountsHaveNotBeenUpdated() {
        if (source != null) {
            verify(source, never()).setBalance(any());
        }
        if (target != null) {
            verify(target, never()).setBalance(any());
        }
        verify(accountRepository, never()).save(any());
    }

    private void thenAccountHaveBeenUpdated(final Account account, final BigDecimal newBalance) {
        InOrder inOrder = inOrder(accountRepository, account);
        inOrder.verify(account).setBalance(newBalance);
        inOrder.verify(accountRepository).save(source);
    }

    private void thenTheResultTransactionIsAsExpected() {
        assertThat(actResult).isEqualTo(transaction);
    }

    private void thenExceptionIsThrown(final Class<? extends Exception> exc) {
        assertThat(actThrown).isInstanceOf(exc);
    }

}