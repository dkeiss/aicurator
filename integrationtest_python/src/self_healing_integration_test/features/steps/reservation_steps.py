"""Behave step implementations for the reservation UI feature."""

from __future__ import annotations

from behave import given, then, when

from self_healing_integration_test.config.data import Reservation as ReservationDto
from self_healing_integration_test.pages.reservation import ReservationPage
from self_healing_integration_test.pages.basepage import call_page_method, current_page, expect_page, open_relative


def _ensure_reservation_page(context) -> ReservationPage:
    try:
        return current_page(context.store, ReservationPage)
    except RuntimeError:
        return expect_page(context, context.store, ReservationPage, ReservationPage.DEFAULT_PATH)

@given("the reservation page is shown")
def the_reservation_page_is_shown(context) -> None:
    expect_page(context, context.store, ReservationPage, ReservationPage.DEFAULT_PATH)

@given("the reservation page v2 is shown")
@then("the reservation page v2 is shown")
def the_reservation_page_v2_is_shown(context) -> None:
    open_relative(context, context.settings, ReservationPage.PATH_V2)
    expect_page(context, context.store, ReservationPage, ReservationPage.PATH_V2)


@when("searching for a taxi")
def searching_for_a_taxi(context) -> None:
    page = _ensure_reservation_page(context)
    entry = context.table.rows[0].as_dict()
    call_page_method(context, page, "set_date", entry["date"])
    call_page_method(context, page, "set_departure", entry["departure"])
    call_page_method(context, page, "set_earliest_start_time", entry["earliestStartTime"])
    call_page_method(context, page, "set_destination", entry["destination"])
    call_page_method(context, page, "set_latest_start_time", entry["latestStartTime"])
    call_page_method(context, page, "submit_search")


@when("a reservation is made")
def a_reservation_is_made(context) -> None:
    page = _ensure_reservation_page(context)
    call_page_method(context, page, "submit_reservation")


@when("joining the first reservation")
def joining_the_first_reservation(context) -> None:
    page = _ensure_reservation_page(context)
    reservation = context.store.reservation
    if reservation is None or reservation.id is None:
        raise RuntimeError("No reservation available in the scenario store")
    call_page_method(context, page, "click_join_button", reservation.id)


@then("no taxis are available")
def no_taxis_are_available(context) -> None:
    page = _ensure_reservation_page(context)
    message = call_page_method(context, page, "alert_success_message")
    assert "No reservations found" in message


@then("the reserve option is visible")
def reserve_option_is_visible(context) -> None:
    page = _ensure_reservation_page(context)
    visible = call_page_method(context, page, "reserve_button_is_displayed")
    assert visible is True


@then("the reserve option is invisible")
def reserve_option_is_invisible(context) -> None:
    page = _ensure_reservation_page(context)
    visible = call_page_method(context, page, "reserve_button_is_displayed")
    assert visible is False


@then("the reservation is shown in the list")
def the_reservation_is_shown_in_the_list(context) -> None:
    page = _ensure_reservation_page(context)
    message = call_page_method(context, page, "alert_success_message")
    assert "Reservation created successfully" in message
    reservation_id = call_page_method(context, page, "reservation_id", 0)
    context.store.reservation = ReservationDto(id=int(reservation_id))


@then("the join option is invisible")
def the_join_option_is_invisible(context) -> None:
    page = _ensure_reservation_page(context)
    reservation = context.store.reservation
    if reservation is None or reservation.id is None:
        raise RuntimeError("No reservation available in the scenario store")
    exists = call_page_method(context, page, "join_button_exists", reservation.id)
    assert not exists


@then("taxis are available")
def taxis_are_available(context) -> None:
    page = _ensure_reservation_page(context)
    reservations = call_page_method(context, page, "reservations")
    assert len(reservations) > 0


@then("the current user is added to the participants")
def the_current_user_is_added_to_the_participants(context) -> None:
    page = _ensure_reservation_page(context)
    reservations = call_page_method(context, page, "reservations")
    if len(reservations) < 2:
        raise AssertionError("Expected at least two reservation entries")
    user = context.store.user
    if user is None:
        raise RuntimeError("No user stored in scenario")
    assert user.username in reservations[1]


@then("a join notification is shown")
def a_join_notification_is_shown(context) -> None:
    page = _ensure_reservation_page(context)
    message = call_page_method(context, page, "alert_info_message")
    assert "There is an update for your reservation" in message

