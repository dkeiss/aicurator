Feature: Registration
  As a user
  I would like to register at the website,
  to make reservations for shared taxis.

  Background:
    Given self-healing for locators is enabled

  Scenario: Registration (V1)
    # This scenario doesn't need self-healing
    Given the registration page is open
    When the user registers
    Then the registration is successful

  Scenario: Registration (V2)
    # This scenario need self-healing
    # 1. The style is changed
    # 2. The the button uses different classes
    Given the registration page v2 is open
    When the user registers
    Then the registration is successful
