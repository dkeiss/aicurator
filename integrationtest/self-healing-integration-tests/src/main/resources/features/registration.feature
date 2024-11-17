Feature: Registration
  As a student,
  I would like to explore the healing capabilities of AiCurator and Healenium using a realistic registration scenario
  to determine if their use could be beneficial in practice.

  Background:
    Given self-healing for locators is enabled

  Scenario: Registration
    # This scenario don't need self-healing
    Given the register page is open
    When the user registers
    Then the registration is successful

  Scenario: Registration with changed username field and button (v2)
    # In this scenario the username field is changed and the button is changed
    # Semantically the fields are the same, but the locators are different
    Given the register page v2 is open
    When the user registers
    Then the registration is successful
