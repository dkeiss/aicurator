Feature: Reservation
  As a logged-in user
  I would like to be able to make reservations for collective tickets on certain routes during specific time periods
  in order to take advantage of any discounts.

  Background:
    Given self-healing for locators is enabled
    And a logged-in customer

  Scenario: Do reservation when no taxis are available and get updates when someone joins
    # This scenario don't need self-healing
    Given no reservations exist
    And the reservation page is shown
    When search for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then no taxis are available
    And reserve is visible
    When reservation is done
    Then reservation is created successfully
    And join is invisible
    When somebody joins the reservation
    Then join notification is shown

  Scenario: Do reservation when no taxis are available and get updates when someone joins (V2)
    # This scenario need self-healing
    # The IDs for search and reservation are not available anymore
    # There is to find a complex css locator to access the elements
    Given no reservations exist
    And the reservation page v2 is shown
    When search for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then no taxis are available
    And reserve is visible
    When reservation is done
    Then reservation is created successfully
    And join is invisible
    When somebody joins the reservation
    Then join notification is shown

  Scenario: Search with available taxis and join
    # This scenario don't need self-healing
    Given no reservations exist
    And available reservations from another user
      | departure | destination | date       | startTime |
      | Station A | Station B   | 2024-11-07 | 10:00     |
    And the reservation page v2 is shown
    When search for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then taxis are available
    And reserve is invisible
    When join first reservation
    Then the current user is added to participants

  Scenario: Search with available taxis and join (V2)
    # This scenario need self-healing
    # The IDs for search and reservation are not available anymore
    # There is to find a complex css locator to access the elements
    # It's more difficult for autohealing since reserve is invisible
    Given no reservations exist
    And available reservations from another user
      | departure | destination | date       | startTime |
      | Station A | Station B   | 2024-11-07 | 10:00     |
    And the reservation page v2 is shown
    When search for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then taxis are available
    And reserve is invisible
    When join first reservation
    Then the current user is added to participants
