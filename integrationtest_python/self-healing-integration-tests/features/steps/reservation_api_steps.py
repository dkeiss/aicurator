"""API-focused step implementations shared across features."""

from __future__ import annotations

import base64
from typing import Dict, List

from behave import given, then, when

from taxishare.api import (
    create_reservation,
    delete_reservations,
    join_reservation,
    login_user,
    register_new_user,
    search_reservations,
    subscribe_to_updates,
)
from taxishare.data import Reservation as ReservationDto


def _table_as_dicts(context) -> List[Dict[str, str]]:
    if not hasattr(context, "table") or context.table is None:
        raise RuntimeError("Step requires a data table")
    return [row.as_dict() for row in context.table.rows]


@given("a logged-in customer via API")
def a_logged_in_customer_via_api(context) -> None:
    register_new_user(context.http_client, context.store)
    login_user(context.http_client, context.store)


@given("no reservations exist")
def no_reservations_exist(context) -> None:
    a_logged_in_customer_via_api(context)
    delete_reservations(context.http_client, context.store)


@given("there are available reservations from another user")
def there_are_available_reservations_from_another_user(context) -> None:
    for entry in _table_as_dicts(context):
        register_new_user(context.http_client, context.store)
        login_user(context.http_client, context.store)
        reservation = ReservationDto(
            date=entry.get("date"),
            departure=entry.get("departure"),
            destination=entry.get("destination"),
            start_time=entry.get("startTime"),
        )
        create_reservation(context.http_client, context.store, reservation)
        context.store.reservation = ReservationDto.from_payload(context.store.response.json())


@given("reservation from different user")
def reservation_from_different_user(context) -> None:
    there_are_available_reservations_from_another_user(context)


@when("the user creates a new reservation")
def the_user_creates_a_new_reservation(context) -> None:
    rows = _table_as_dicts(context)
    entry = rows[0]
    reservation = ReservationDto(
        date=entry.get("date"),
        departure=entry.get("departure"),
        destination=entry.get("destination"),
        start_time=entry.get("startTime"),
    )
    create_reservation(context.http_client, context.store, reservation)


@then("the reservation is created successfully")
def the_reservation_is_created_successfully(context) -> None:
    response = context.store.response
    response.raise_for_status()
    reservation = ReservationDto.from_payload(response.json())
    context.store.reservation = reservation


@when("the user joins the reservation")
@when("somebody joins the reservation")
def the_user_joins_the_reservation(context) -> None:
    reservation = context.store.reservation
    if reservation is None or reservation.id is None:
        raise RuntimeError("No reservation available in the scenario store")
    join_reservation(context.http_client, context.store, reservation.id)


@then("the user is added to the reservation successfully")
def the_user_is_added_to_the_reservation_successfully(context) -> None:
    response = context.store.response
    response.raise_for_status()
    reservation = ReservationDto.from_payload(response.json())
    if not reservation.participants or len(reservation.participants) < 2:
        raise AssertionError("The number of participants should be greater than or equal to 2")
    context.store.reservation = reservation


@when("the user searches for reservations")
def the_user_searches_for_reservations(context) -> None:
    rows = _table_as_dicts(context)
    params = {
        key: base64.b64encode(value.encode()).decode() for key, value in rows[0].items()
    }
    search_reservations(context.http_client, context.store, params)


@then("the available reservations are returned")
def the_available_reservations_are_returned(context) -> None:
    response = context.store.response
    response.raise_for_status()
    reservations = response.json()
    if not isinstance(reservations, list) or not reservations:
        raise AssertionError("Expected at least one reservation to be returned")


@when("the user register for updates")
def the_user_register_for_updates(context) -> None:
    reservation = context.store.reservation
    if reservation is None or reservation.id is None:
        raise RuntimeError("No reservation available in the scenario store")
    subscribe_to_updates(context.http_client, context.store, reservation.id)


@then("there are {updates:d} updates available")
def there_are_updates_available(context, updates: int) -> None:
    updates_stream = context.store.reservation_updates
    if updates_stream is None:
        raise RuntimeError("Reservation updates stream not available")
    count = 0
    for line in updates_stream:
        if line:
            count += 1
        if count >= updates:
            break
    if count != updates:
        raise AssertionError(f"Expected {updates} updates but received {count}")

