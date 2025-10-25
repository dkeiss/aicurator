from __future__ import annotations

import logging
from dataclasses import dataclass
from typing import Optional

from self_healing_integration_tests.features.support.self_healing import ChatGPTStepHealer

LOGGER = logging.getLogger(__name__)


@dataclass
class StepHealer:
    """Behave compatible step healer that mirrors the Java implementation."""

    chatgpt_healer: ChatGPTStepHealer

    def heal(self, scenario_name: str, step_name: str, error: str, feature_text: str) -> Optional[str]:
        LOGGER.info("Triggering ChatGPT based step healing for '%s'", step_name)
        suggestion = self.chatgpt_healer.heal_step(scenario_name, step_name, error, feature_text)
        if suggestion:
            LOGGER.info("ChatGPT suggestion: %s", suggestion)
        return suggestion
