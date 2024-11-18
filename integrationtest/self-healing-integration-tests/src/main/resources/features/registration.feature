Feature: Registration
  As a student,
  I would like to explore the healing capabilities of AiCurator and Healenium using a realistic registration scenario
  to determine if their use could be beneficial in practice.

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
    # 2. The username field name is changed to email
    # 3. The the button uses different classes
    Given the registration page v2 is open
    When the user registers
    Then the registration is successful
