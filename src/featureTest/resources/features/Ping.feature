Feature: A ping feature

  Scenario: I call ping endpoint
    When I ping the service
    Then I get response status of 200
    And the response body is "pong"
