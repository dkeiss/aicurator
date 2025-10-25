from __future__ import annotations

from behave import given, then, when

from ..pages.registration_page import RegistrationPage
from ..support.data_factory import User, random_user


def _driver(context):
    driver = context.browser_manager.create()
    context.driver = driver
    return driver


def _healer(context):
    return context.locator_healer if context.store.healing_enabled else None


def _open_registration_page(context, url_path: str) -> RegistrationPage:
    driver = _driver(context)
    page = RegistrationPage(driver=driver, base_url=context.frontend_url, healer=_healer(context), url_path=url_path)
    page.open()
    context.store.page = page
    return page


@given("the registration page is open")
def step_registration_page_open(context) -> None:
    _open_registration_page(context, "register")


@given("the registration page v2 is open")
def step_registration_page_v2_open(context) -> None:
    _open_registration_page(context, "registerV2")


@when("the user registers")
def step_user_registers(context) -> None:
    page: RegistrationPage = context.store.page
    user: User = context.store.user if isinstance(context.store.user, User) else random_user()
    context.store.user = user
    page.register(user.username, user.email, user.password)


@then("the registration is successful")
def step_registration_successful(context) -> None:
    page: RegistrationPage = context.store.page
    message = page.success_message()
    assert message.strip() == "User registered successfully!", message
