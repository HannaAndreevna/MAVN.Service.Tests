Feature: Hotel Stay Bonus
  As a partner I want to trigger a bonus to a customer staying in hotel

  Background:
    Given there is a partner
    And there is a customer

  @payments
  Scenario: Hotel Stay Bonus
    Given partner searches for the customer
    And sends a message
    And customer receives the message
    When partner creates a payment request
    And customer approves the payment request
    And partner executes the request
    Then balance is as expected
    When partner triggers a bonus
    Then balance reflects the bonus arrived
