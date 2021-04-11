package com.vydra.featuretest.steps;

import com.vydra.featuretest.steps.states.TransactionContextState;
import com.vydra.transactionservice.persistence.model.Account;
import com.vydra.transactionservice.persistence.repositories.AccountRepository;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AccountSteps {

    private static final Currency DEFAULT_CURERNCY = Currency.getInstance("GBP");

    @Autowired
    private AccountRepository repository;

    @Autowired
    private TransactionContextState state;

    @Given("{word} account exists")
    public void createAccount(final String type) {
        state.getAccounts().putIfAbsent(type, repository.save(new Account(BigDecimal.ZERO, DEFAULT_CURERNCY)));
    }


    @Given("{word} account has balance less than the transaction amount")
    public void balanceRelativeToTransactionLess(final String accountType) {
        relativeBalanceSet(accountType, (target) -> target.subtract(BigDecimal.ONE));
    }

    @Given("{word} account has balance greater or equal to the transaction amount")
    public void balanceRelativeToTransactionGreaterOrEqual(final String accountType) {
        relativeBalanceSet(accountType, (target) -> target.add(BigDecimal.valueOf(new Random().nextInt(2))));
    }

    @Given("{word} or {word} account do not exist")
    public void accountsDontExist(final String acc1, final String acc2) {
        deleteAccount(acc1);
        deleteAccount(acc2);
    }

    @Given("both {word} and {word} accounts are the same")
    public void accountsAreTheSame(final String acc1, final String acc2) {
        Account account1 = state.getAccounts().get(acc1);
        Account account2 = state.getAccounts().get(acc2);

        // if both were already present, ditch the second one
        if (account1 != null && account2 != null) {
            deleteAccount(acc2);
            account2 = null;
        }

        if (account1 != null) {
            state.setSource(acc1);
            state.setTarget(acc1);
        }

        if (account2 != null) {
            state.setSource(acc2);
            state.setTarget(acc2);
        }
    }

    @Then("the balance of the {word} account is {action}")
    public void accountActioned(final String type,
                                final BalanceAction action) {
        BigDecimal lastKnownBalance = state.getAccounts().get(type).getBalance();
        BigDecimal actBalance = repository.findById(state.getAccountId(type)).map(Account::getBalance)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
        assertThat(actBalance).isEqualByComparingTo(action.apply(lastKnownBalance, state.getAmount()));
    }

    @Then("the balance of {word} account should remain the same")
    public void balanceIsTheSame(final String account) {
        BigDecimal expBalance = state.getAccounts().get(account).getBalance();
        BigDecimal actBalance = repository.findById(state.getAccountId(account)).map(Account::getBalance)
                .orElseThrow(() -> new IllegalStateException("Account not found"));
        assertThat(expBalance).isEqualByComparingTo(actBalance);
    }

    private void deleteAccount(final String acc) {
        Account deleted = state.getAccounts().remove(acc);
        if (deleted != null) {
            repository.deleteById(deleted.getId());
        }
    }

    private void relativeBalanceSet(final String accountType, final RelativeBalance action) {
        Account account = repository.findById(state.getAccountId(accountType))
                .orElseThrow(() -> new IllegalStateException("Account does not exist"));
        account.setBalance(action.apply(state.getAmount()));
        state.getAccounts().put(accountType, repository.save(account));
    }

    @ParameterType("credited|debited")
    public BalanceAction action(final String action) {
        switch(action) {
            case "debited":
                return BigDecimal::subtract;
            case "credited":
                return BigDecimal::add;
            default:
                throw new IllegalArgumentException(format("Action %s does not exist", action));
        }
    }

    @FunctionalInterface
    interface BalanceAction extends BiFunction<BigDecimal, BigDecimal, BigDecimal> {
        @Override
        BigDecimal apply(BigDecimal original, BigDecimal change);
    }

    @FunctionalInterface
    interface RelativeBalance extends Function< BigDecimal, BigDecimal> {
        @Override
        BigDecimal apply(BigDecimal target);
    }

}
