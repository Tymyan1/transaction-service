Feature: Allows making financial transactions between two bank accounts.

  Background:
    Given a refreshed state
      And a transaction request from source to target

  Scenario: AC1 Happy path for money transfer between two accounts
    Given source account exists
      And target account exists
      And source account has balance greater or equal to the transaction amount
    When a transaction request is received
    Then the balance of the source account is debited
      And the balance of the target account is credited

    Scenario: AC2 Insufficient balance to process money transfer
      Given source account exists
        And target account exists
        And source account has balance less than the transaction amount
      When a transaction request is received
      Then the balance of source account should remain the same
        And the balance of target account should remain the same
        And the client of the API should receive an error

  Scenario: AC3 - Transfer between same account
      Given source account exists
        And both source and target accounts are the same
      When a transaction request is received
      Then the balance of source account should remain the same
        And the client of the API should receive an error

  Scenario: AC4 - One or more of the accounts does not exist
      Given source or target account do not exist
      When a transaction request is received
      #Then the balance of the existing account should remain the same - since the accounts don't exist, this makes no sense
      Then the client of the API should receive an error

  Scenario: Negative money transfer
    Given source account exists
      And target account exists
      And the transaction amount is negative
    When a transaction request is received
    Then the client of the API should receive an error
