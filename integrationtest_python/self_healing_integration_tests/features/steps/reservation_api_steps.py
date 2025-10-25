from __future__ import annotations

from typing import Dict, List

from behave import given, then, when

from ..support.data_factory import Reservation
from ..support.scenario_store import ScenarioStore
from .login_api_steps import login_user_via_api
from .registration_api_steps import register_user_via_api


def _reservation_from_row(row: Dict[str, str]) -> Reservation:
    return Reservation(
        date=row["date"],
        departure=row["departure"],
        destination=row["destination"],
        startTime=row.get("startTime") or row.get("start_time") or "",
    )


@given("no reservations exist")
def step_no_reservations_exist(context) -> None:
    login_user_via_api(context)
    response = context.api_client.delete("/api/reservations")
    if response.status_code not in (200, 204):
        raise AssertionError(f"Failed to delete reservations: {response.status_code} {response.text}")


@given("there are available reservations from another user")
def step_available_reservations_from_another_user(context) -> None:
    for row in context.table:
        _create_reservation_for_another_user(context, row.as_dict())


def _create_reservation_for_another_user(context, row: Dict[str, str]) -> None:
    original_user = context.store.user
    original_token = context.store.jwt_token
    temp_user = register_user_via_api(context, persist=False)
    login_user_via_api(context, temp_user)
    reservation = _reservation_from_row(row)
    response = context.api_client.post("/api/reservations", reservation.as_payload())
    if response.status_code != 200:
        raise AssertionError(f"Failed to create reservation: {response.status_code} {response.text}")
    context.store.user = original_user
    context.store.jwt_token = original_token


@when("the user creates a new reservation")
def step_user_creates_new_reservation(context) -> None:
    login_user_via_api(context)
    row = context.table[0].as_dict()
    reservation = _reservation_from_row(row)
    response = context.api_client.post("/api/reservations", reservation.as_payload())
    if response.status_code != 200:
        raise AssertionError(f"Failed to create reservation: {response.status_code} {response.text}")
    context.store.reservation = response.json()


@when("the user joins the reservation")
@when("somebody joins the reservation")
def step_user_joins_reservation(context) -> None:
    reservation = context.store.reservation
    if reservation is None:
        raise AssertionError("No reservation available in store")
    login_user_via_api(context)
    reservation_id = reservation.get("id")
    response = context.api_client.put(f"/api/reservations/{reservation_id}/join")
    if response.status_code != 200:
        raise AssertionError(f"Failed to join reservation: {response.status_code} {response.text}")
    context.store.response = response


@when("the user searches for reservations")
def step_user_searches_for_reservations(context) -> None:
    login_user_via_api(context)
    params = context.api_client.encode_query(context.table[0].as_dict())
    response = context.api_client.get("/api/reservations", params=params)
    if response.status_code != 200:
        raise AssertionError(f"Reservation search failed: {response.status_code} {response.text}")


@then("the reservation is created successfully")
def step_reservation_created_successfully(context) -> None:
    response = context.store.response
    if response is None:
        raise AssertionError("No response available for reservation creation")
    if response.status_code != 200:
        raise AssertionError(f"Unexpected status code: {response.status_code}")
    context.store.reservation = response.json()


@then("the user is added to the reservation successfully")
def step_user_added_to_reservation(context) -> None:
    response = context.store.response
    if response is None:
        raise AssertionError("No response available for join action")
    body = response.json()
    participants = body.get("participants", [])
    if len(participants) < 2:
        raise AssertionError("Expected at least 2 participants after joining")


@then("the available reservations are returned")
def step_available_reservations_returned(context) -> None:
    response = context.store.response
    if response is None:
        raise AssertionError("No response available for search")
    reservations = response.json()
    if not isinstance(reservations, list) or not reservations:
        raise AssertionError("Expected a list of reservations in response")
