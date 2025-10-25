from __future__ import annotations

from behave import then, when
from selenium.webdriver.common.by import By

from ..pages.reservation_page import ReservationPage
from ..support.data_factory import User


def _page(context) -> ReservationPage:
    page = context.store.page
    if not isinstance(page, ReservationPage):
        driver = context.browser_manager.create()
        context.driver = driver
        healer = context.locator_healer if context.store.healing_enabled else None
        page = ReservationPage(driver=driver, base_url=context.frontend_url, healer=healer)
        context.store.page = page
    return page


@when("searching for a taxi")
def step_searching_for_taxi(context) -> None:
    page = _page(context)
    entry = context.table[0].as_dict()
    page.search(
        date=entry["date"],
        departure=entry["departure"],
        destination=entry["destination"],
        earliest=entry["earliestStartTime"],
        latest=entry["latestStartTime"],
    )


@when("a reservation is made")
def step_reservation_is_made(context) -> None:
    page = _page(context)
    page.reserve()


@when("joining the first reservation")
def step_joining_first_reservation(context) -> None:
    page = _page(context)
    reservation_id = context.store.reservation.get("id") if context.store.reservation else None
    if reservation_id is None:
        ids = page.reservation_ids()
        if not ids:
            raise AssertionError("No reservation ids available")
        reservation_id = ids[0]
        context.store.reservation = {"id": reservation_id}
    page.join(int(reservation_id))


@then("the reserve option is visible")
def step_reserve_option_visible(context) -> None:
    page = _page(context)
    assert page.reserve_button_visible(), "Reserve button should be visible"


@then("the reserve option is invisible")
def step_reserve_option_invisible(context) -> None:
    page = _page(context)
    assert not page.reserve_button_visible(), "Reserve button should be hidden"


@then("the reservation is shown in the list")
def step_reservation_shown_in_list(context) -> None:
    page = _page(context)
    message = page.success_message()
    assert "Reservation created successfully" in message
    ids = page.reservation_ids()
    if not ids:
        raise AssertionError("No reservations rendered")
    context.store.reservation = {"id": ids[0]}


@then("taxis are available")
def step_taxis_are_available(context) -> None:
    page = _page(context)
    assert page.reservations_text(), "Expected at least one reservation entry"


@then("no taxis are available")
def step_no_taxis_available(context) -> None:
    page = _page(context)
    message = page.success_message()
    assert "No reservations found" in message


@then("the join option is invisible")
def step_join_option_invisible(context) -> None:
    page = _page(context)
    reservation = context.store.reservation or {}
    reservation_id = reservation.get("id")
    if reservation_id is None:
        raise AssertionError("Reservation id not available")
    table = page.find(page.reservation_table)
    elements = table.find_elements(By.ID, f"joinButton{reservation_id}")
    assert not elements, "Join button should not exist"


@then("the current user is added to the participants")
def step_current_user_added(context) -> None:
    page = _page(context)
    user: User = context.store.user
    reservations = page.reservations_text()
    if len(reservations) < 2:
        raise AssertionError("Expected at least two reservation entries")
    assert user.username in reservations[1]


@then("a join notification is shown")
def step_join_notification_is_shown(context) -> None:
    page = _page(context)
    message = page.info_message()
    assert "There is an update for your reservation" in message
