Feature: Reservation API
  As a logged-in user
  I would like to be able to make reservations for collective tickets on certain routes during specific time periods
  in order to take advantage of any discounts.

  Scenario: Create a new reservation and other join it
    Given a logged-in customer via API
    And no reservations exist
    When the user creates a new reservation
      | date       | departure | destination | startTime |
      | 2024-11-07 | Station A | Station B   | 10:00     |
    Then the reservation is created successfully
    Given a logged-in customer via API
    When the user joins the reservation
    Then the user is added to the reservation successfully
    When the user searches for reservations
      | departure | destination | date       | earliestStartTime | latestStartTime |
      | Station A | Station B   | 2024-11-07 | 09:00             | 11:00           |
    Then the available reservations are returned