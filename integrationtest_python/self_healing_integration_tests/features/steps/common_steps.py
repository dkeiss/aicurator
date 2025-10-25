from __future__ import annotations

from behave import given


@given("self-healing for locators is enabled")
def enable_locator_healing(context) -> None:
    context.store.healing_enabled = True


@given("self-healing for locators is disabled")
def disable_locator_healing(context) -> None:
    context.store.healing_enabled = False


@given("a new browser session")
def new_browser_session(context) -> None:
    driver = context.browser_manager.create()
    context.driver = driver
    driver.delete_all_cookies()
