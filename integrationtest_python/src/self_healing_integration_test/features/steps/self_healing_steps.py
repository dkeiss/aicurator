"""Steps that configure the healing approach for a scenario."""

from __future__ import annotations

from behave import given

from self_healing_integration_test.config.store import HealingApproach


@given("self-healing for locators is enabled")
def enable_locator_self_healing(context) -> None:
    if context.browser_manager.is_loaded():
        raise RuntimeError("Enable healing before interacting with the browser")
    context.store.healing_approach = HealingApproach.AICURATOR

