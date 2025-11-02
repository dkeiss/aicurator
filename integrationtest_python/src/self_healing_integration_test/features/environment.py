"""Behave environment hooks that enable AiCurator self-healing and scenario fixtures."""

from __future__ import annotations

from behave.model_core import Status

from aicurator.selenium.auto_healer import AutoHealer

from self_healing_integration_test.config.browser import BrowserManager
from self_healing_integration_test.config.config import load_settings
from self_healing_integration_test.config.http import HttpClient
from self_healing_integration_test.config.store import ScenarioStore


def before_all(context) -> None:
    context.settings = load_settings()
    context.auto_healer = AutoHealer()
    context.browser_manager = BrowserManager(context.settings)


def before_scenario(context, scenario) -> None:
    context.scenario = scenario
    context.store = ScenarioStore()
    context.http_client = HttpClient(context.settings.backend_url)
    context.healing_results = []
    context.last_healing_prompt = None
    context.last_healing_response = None


def after_step(context, step) -> None:
    if step.status != Status.failed:
        return

    result = context.auto_healer.handle_failure(context, context.scenario)
    if result:
        context.healing_results.append(result)
        context.last_healing_prompt = result.prompt
        context.last_healing_response = result.response


def after_scenario(context, scenario) -> None:
    context.browser_manager.quit()
    if hasattr(context, "http_client"):
        context.http_client.close()
