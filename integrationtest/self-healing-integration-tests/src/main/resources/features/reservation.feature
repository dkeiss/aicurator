Feature: Reservation
  As a logged-in user,
  I would like to be able to make reservations for collective tickets on certain routes during specific time periods
  in order to take advantage of any discounts.

  Background:
    Given self-healing for locators is enabled
    And a logged-in customer

  Scenario: Made a reservation and get updates (V1)
    # This scenario doesn't need self-healing
    Given no reservations exist
    And the reservation page is shown
    When searching for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then no taxis are available
    And the reserve option is visible
    When a reservation is made
    Then the reservation is shown in the list
    And the join option is invisible
    When somebody joins the reservation
    Then a join notification is shown

  Scenario: Made a reservation and get updates (V2)
    # This scenario needs self-healing
    # 1. The ID for the search button is no longer available; only type and name are present.
    # 2. The ID for the visible reserve button is no longer available; only type and name are present.
    Given no reservations exist
    And the reservation page v2 is shown
    When searching for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then no taxis are available
    And the reserve option is visible
    When a reservation is made
    Then the reservation is shown in the list
    And the join option is invisible
    When somebody joins the reservation
    Then a join notification is shown

  Scenario: Search for available taxis and join (V1)
    # This scenario doesn't need self-healing
    Given no reservations exist
    And there are available reservations from another user
      | departure | destination | date       | startTime |
      | Station A | Station B   | 2024-11-07 | 10:00     |
    And the reservation page is shown
    When searching for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then taxis are available
    And the reserve option is invisible
    When joining the first reservation
    Then the current user is added to the participants

  Scenario: Search for available taxis and join (V2)
    # This scenario needs self-healing
    # 1. The ID for the search button is no longer available; only type and name are present.
    # 2. The ID for the invisible reserve button is no longer available; only type and name are present.
    Given no reservations exist
    And there are available reservations from another user
      | departure | destination | date       | startTime |
      | Station A | Station B   | 2024-11-07 | 10:00     |
    And the reservation page v2 is shown
    When searching for a taxi
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 07.11.2024 | 09:00             | 11:00           |
    Then taxis are available
    And the reserve option is invisible
    When joining the first reservation
    Then the current user is added to the participants