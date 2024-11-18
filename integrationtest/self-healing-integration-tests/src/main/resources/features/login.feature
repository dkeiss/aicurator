Feature: Login
  As a student,
  I would like to explore the healing capabilities of AiCurator and Healenium using a realistic login scenario
  to determine if their use could be beneficial in practice.

  Background:
    Given self-healing for locators is enabled
    And a new registered user
    And a new browser session

  Scenario: Login (V1)
    # This scenario doesn't need self-healing
    Given the login page is open
    When the user logs in
    Then the reservation page is shown

  Scenario: Login (V2)
    # This scenario need self-healing
    # 1. The username field name is changed to email
    Given the login page v2 is open
    When the user logs in with email
    Then the reservation page is shown

  Scenario: Login (V3)
    # This scenario need self-healing
    # 1. The style is changed
    # 2. Login button uses a different class and has an ID which should be used
    # 3. Google login button with ID is added
    Given the login page v3 is open
    When the user logs in with email
    Then the reservation page is shown

  Scenario: Login (V4)
    # This scenario need self-healing
    # 1. The style is changed
    # 2. Login button uses a different class and has an ID which should be used
    # 3. Google login button without ID is added
    # This is an adjusted scenario designed to fail Healenium
    # It will give the Google button a higher rank because of the missing ID, similar to the last successful locator
    Given the login page v4 is open
    When the user logs in with email
    Then the reservation page is shown
