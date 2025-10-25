from __future__ import annotations

from behave import given, then, when

from ..pages.login_page import LoginPage
from ..pages.reservation_page import ReservationPage
from ..support.data_factory import User
from ..support.page_factory import current_page
from .registration_api_steps import register_user_via_api


def _driver(context):
    driver = context.browser_manager.create()
    context.driver = driver
    return driver


def _healer(context):
    return context.locator_healer if context.store.healing_enabled else None


def _open_login_page(context, url_path: str) -> LoginPage:
    driver = _driver(context)
    page = LoginPage(driver=driver, base_url=context.frontend_url, healer=_healer(context), url_path=url_path)
    page.open()
    context.store.page = page
    return page


@given("the home page is open")
def step_home_page_open(context) -> None:
    driver = _driver(context)
    driver.get(context.frontend_url)
    context.store.page = None


@given("the login page is open")
def step_login_page_open(context) -> None:
    _open_login_page(context, "login")


@given("the login page v2 is open")
@given("the login page with changed username field is open")
def step_login_page_v2_open(context) -> None:
    _open_login_page(context, "loginV2")


@given("the login page v3 is open")
@given("the login page with changed login button and additional button is open")
def step_login_page_v3_open(context) -> None:
    _open_login_page(context, "loginV3")


@given("the login page v4 is open")
@given("the login page with changed login button and additional button without id is open")
def step_login_page_v4_open(context) -> None:
    _open_login_page(context, "loginV4")


@given("a logged-in customer")
def step_logged_in_customer(context) -> None:
    user = context.store.user if isinstance(context.store.user, User) else register_user_via_api(context)
    step_login_page_open(context)
    step_user_logs_in(context)
    context.store.user = user


@when("the user logs in with invalid credentials")
def step_user_logs_in_with_invalid_credentials(context) -> None:
    page: LoginPage = current_page(context.store)
    page.login("invalidUser", "invalidPassword")


@when("the user logs in")
def step_user_logs_in(context) -> None:
    page: LoginPage = current_page(context.store)
    user: User = context.store.user
    page.login(user.username, user.password)


@when("the user logs in with email")
def step_user_logs_in_with_email(context) -> None:
    page: LoginPage = current_page(context.store)
    user: User = context.store.user
    page.login(user.email, user.password)


@given("the reservation page is shown")
@then("the reservation page is shown")
def step_reservation_page_shown(context) -> None:
    driver = _driver(context)
    page = ReservationPage(driver=driver, base_url=context.frontend_url, healer=_healer(context))
    page.check_url()
    context.store.page = page


@then("the login page is shown")
def step_login_page_shown(context) -> None:
    driver = _driver(context)
    page = LoginPage(driver=driver, base_url=context.frontend_url, healer=_healer(context))
    page.check_url()
    context.store.page = page


@given("the reservation page v2 is shown")
@then("the reservation page v2 is shown")
def step_reservation_page_v2_shown(context) -> None:
    driver = _driver(context)
    page = ReservationPage(driver=driver, base_url=context.frontend_url, healer=_healer(context), url_path="reservationV2")
    page.open()
    page.check_url()
    context.store.page = page
