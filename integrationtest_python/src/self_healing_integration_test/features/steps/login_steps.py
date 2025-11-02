"""Behave step implementations for the login feature."""

from __future__ import annotations

from behave import given, then, when

from self_healing_integration_test.config.api import register_new_user
from self_healing_integration_test.pages.login import LoginPage
from self_healing_integration_test.pages.reservation import ReservationPage
from self_healing_integration_test.pages.basepage import call_page_method, current_page, expect_page, open_relative, open_url


@given("the home page is open")
def the_home_page_is_open(context) -> None:
    open_url(context, context.settings.frontend_url)


@given("the login page is open")
def the_login_page_is_open(context) -> None:
    open_relative(context, context.settings, LoginPage.DEFAULT_PATH)
    expect_page(context, context.store, LoginPage, LoginPage.DEFAULT_PATH)


@given("the login page v2 is open")
@given("the login page with changed username field is open")
def the_login_page_v2_is_open(context) -> None:
    open_relative(context, context.settings, LoginPage.PATH_V2)
    expect_page(context, context.store, LoginPage, LoginPage.PATH_V2)


@given("the login page v3 is open")
@given("the login page with changed login button and additional button is open")
def the_login_page_v3_is_open(context) -> None:
    open_relative(context, context.settings, LoginPage.PATH_V3)
    expect_page(context, context.store, LoginPage, LoginPage.PATH_V3)


@given("the login page v4 is open")
@given("the login page with changed login button and additional button without id is open")
def the_login_page_v4_is_open(context) -> None:
    open_relative(context, context.settings, LoginPage.PATH_V4)
    expect_page(context, context.store, LoginPage, LoginPage.PATH_V4)


@given("a new registered user")
def a_new_registered_user(context) -> None:
    register_new_user(context.http_client, context.store)


@given("a new browser session")
def a_new_browser_session(context) -> None:
    context.browser_manager.delete_all_cookies()


@given("a logged-in customer")
def a_logged_in_customer(context) -> None:
    register_new_user(context.http_client, context.store)
    the_login_page_is_open(context)
    the_user_logs_in(context)


@when("the user logs in")
def the_user_logs_in(context) -> None:
    page = current_page(context.store, LoginPage)
    user = context.store.user
    if user is None:
        raise RuntimeError("No registered user in scenario store")
    call_page_method(context, page, "login", user.username, user.password)


@when("the user logs in with email")
def the_user_logs_in_with_email(context) -> None:
    page = current_page(context.store, LoginPage)
    user = context.store.user
    if user is None:
        raise RuntimeError("No registered user in scenario store")
    call_page_method(context, page, "login", user.email, user.password)


@then("the reservation page is shown")
def the_reservation_page_is_shown(context) -> None:
    expect_page(context, context.store, ReservationPage, ReservationPage.DEFAULT_PATH)


@then("the user receives the message that the login data is invalid")
def the_user_receives_invalid_login_message(context) -> None:
    # The Java implementation leaves this as a no-op placeholder.
    pass

