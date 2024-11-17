Feature: Login
  As a student,
  I would like to explore the healing capabilities of AiCurator and Healenium using a realistic login scenario
  to determine if their use could be beneficial in practice.

  Background:
    Given self-healing for locators is enabled
    And new registered user
    And clean session

  Scenario: Login
    # This scenario don't need self-healing
    Given the login page is open
    When the user logs in
    Then the reservation page is shown

  Scenario: Login v2 with changed username field
    # In this scenario the username change field is changed to email
    # Semantically the field is the same, the self-healing should use the email field
    Given the login page v2 is open
    When the user logs in with email
    Then the reservation page is shown

  Scenario: Login v3 with login button and additional google login button
    # In this scenario we have a different layout which should not affect the test
    # The login button is changed from class to an ID and we have an additional google login button with ID
    # The self-healing should use login button by ID and not the google login button
    Given the login page v3 is open
    When the user logs in with email
    Then the reservation page is shown

  Scenario: Login v4 with changed login button and additional google login button, tricked against Healenium
    # In this scenario we have a different layout which should not affect the test
    # The login button is changed from class to an ID and we have an additional google login button without ID
    # This is an an adjusted scenario to fail Healenium since it will give the google button an higher rank because of the missing ID like in the last successful locator
    Given the login page v4 is open
    When the user logs in with email
    Then the reservation page is shown
