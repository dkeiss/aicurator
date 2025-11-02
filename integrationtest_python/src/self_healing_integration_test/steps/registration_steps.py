"""Behave step implementations for the registration feature."""

from __future__ import annotations

from behave import given, then, when
from faker import Faker

from self_healing_integration_test.pages.registration import RegistrationPage
from self_healing_integration_test.pages.basepage import call_page_method, current_page, expect_page, open_relative

faker = Faker()


@given("the registration page is open")
def registration_page_is_open(context) -> None:
    open_relative(context, context.settings, RegistrationPage.DEFAULT_PATH)
    expect_page(context, context.store, RegistrationPage, RegistrationPage.DEFAULT_PATH)


@given("the registration page v2 is open")
def registration_page_v2_is_open(context) -> None:
    open_relative(context, context.settings, RegistrationPage.PATH_V2)
    expect_page(context, context.store, RegistrationPage, RegistrationPage.PATH_V2)


@when("the user registers")
def the_user_registers(context) -> None:
    page = current_page(context.store, RegistrationPage)
    username = faker.user_name()
    email = faker.email()
    password = faker.password()
    call_page_method(context, page, "register", username, email, password)


@then("the registration is successful")
def the_registration_is_successful(context) -> None:
    page = current_page(context.store, RegistrationPage)
    message = call_page_method(context, page, "registration_success_message")
    assert message == "User registered successfully!", message

