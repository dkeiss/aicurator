from __future__ import annotations

import logging
import os
from pathlib import Path
from typing import Optional

from behave.model_core import Status
from dotenv import load_dotenv

from aicurator.step_healer import StepHealer
from self_healing_integration_tests.features.support.api_client import TaxiShareApiClient
from self_healing_integration_tests.features.support.browser import BrowserManager
from self_healing_integration_tests.features.support.scenario_store import ScenarioStore
from self_healing_integration_tests.features.support.self_healing import (
    ChatGPTLocatorHealer,
    ChatGPTStepHealer,
)

LOGGER = logging.getLogger(__name__)


def before_all(context) -> None:
    load_dotenv()
    context.config.setup_logging()
    context.store = ScenarioStore()
    context.browser_manager = BrowserManager(headless=_as_bool(os.getenv("HEADLESS", "true")))
    context.locator_healer = ChatGPTLocatorHealer()
    context.step_healer = StepHealer(ChatGPTStepHealer())
    context.frontend_url = os.getenv("TAXISHARE_FRONTEND_URL", "http://localhost:3000")
    context.backend_url = os.getenv("TAXISHARE_BACKEND_URL", "http://localhost:8080")
    context.api_client = TaxiShareApiClient(context.backend_url, context.store)
    context.self_healing_enabled = context.config.userdata.getbool("step_healing_enabled", False)
    context.default_locator_healing = False
    context.driver = None
    LOGGER.info("Behave environment initialized with frontend=%s backend=%s", context.frontend_url, context.backend_url)


def before_scenario(context, scenario) -> None:
    context.store.reset()
    context.driver = None
    context.store.healing_enabled = context.default_locator_healing


def after_scenario(context, scenario) -> None:
    if context.driver:
        context.browser_manager.quit()
        context.driver = None

    if scenario.status == Status.failed and context.self_healing_enabled:
        failed_step = _find_failed_step(scenario)
        if failed_step is None:
            return
        feature_text = _read_feature(scenario)
        suggestion = context.step_healer.heal(
            scenario.name,
            failed_step.name,
            failed_step.error_message or "",
            feature_text,
        )
        if suggestion:
            scenario.attach(suggestion, "text/plain", "ChatGPT self-healing suggestion")


def _find_failed_step(scenario) -> Optional[any]:
    for step in scenario.steps:
        if step.status == Status.failed:
            return step
    return None


def _read_feature(scenario) -> str:
    feature_file = Path(scenario.feature.filename)
    try:
        return feature_file.read_text(encoding="utf-8")
    except OSError:
        LOGGER.debug("Unable to read feature file %s", feature_file, exc_info=True)
        return scenario.feature.name


def _as_bool(value: str) -> bool:
    return value.lower() in {"1", "true", "yes", "on"}
