Feature: Vouchers
  As an admin
  I want customer to be able to buy a voucher

Background:
  Given there is a spend rule with price 150.0
  And there is a customer with balance no less than 1000.0

Scenario: buying a voucher
  Given I upload a csv file 'vouchers_001.csv' with voucher code
  When customer buys the voucher
  Then the voucher is related to the customer
  And voucher status is Reserved
  And customer balance is decreased by the price of voucher
  And operations history contains a voucher transaction
  And soldCount on the spend rule is increased
  And voucher status is Sold
  And MVN internal gateway balance is decreased by the price of voucher
  And operations history contains voucher purchase
