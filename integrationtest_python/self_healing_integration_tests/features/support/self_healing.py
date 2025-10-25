from __future__ import annotations

import json
import logging
import os
from dataclasses import dataclass
from typing import Optional, Tuple

from bs4 import BeautifulSoup
from openai import OpenAI
from tenacity import retry, stop_after_attempt, wait_exponential

LOGGER = logging.getLogger(__name__)


@dataclass(frozen=True)
class HealableLocator:
    by: str
    value: str
    description: str = ""

    def locate(self, driver):
        return driver.find_element(self.by, self.value)

    def serialize(self) -> dict:
        return {"by": self.by, "value": self.value, "description": self.description}


class ChatGPTLocatorHealer:
    """Uses the OpenAI API to propose alternative locators."""

    def __init__(self, model: str = "gpt-4o-mini", api_key: Optional[str] = None):
        self.api_key = api_key or os.getenv("OPENAI_API_KEY")
        self.model = model
        self._client: Optional[OpenAI] = None
        if self.api_key:
            self._client = OpenAI(api_key=self.api_key)
        if not self.api_key:
            LOGGER.warning(
                "OPENAI_API_KEY is not set. Locator self-healing will operate in dry-run mode."
            )

    @retry(wait=wait_exponential(min=1, max=10), stop=stop_after_attempt(3), reraise=False)
    def _ask_chatgpt(self, html: str, locator: HealableLocator) -> Optional[Tuple[str, str, str]]:
        if not self._client:
            return None
        system_prompt = (
            "Du bist ein hilfreicher Test-Automatisierungs-Assistent. "
            "Analysiere den gegebenen HTML-Code und schlage einen alternativen Selenium-Locator vor."
        )
        user_prompt = json.dumps(
            {
                "html": html,
                "failed_locator": locator.serialize(),
                "instructions": "Finde das beste CSS- oder XPath-Locator-Paar für das beschriebene Element."
            },
            ensure_ascii=False,
        )
        response = self._client.responses.create(
            model=self.model,
            input=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
            max_output_tokens=600,
        )
        if not response or not response.output_text:
            return None
        try:
            payload = json.loads(response.output_text)
            by = payload.get("by")
            value = payload.get("value")
            description = payload.get("description", "Suggested by ChatGPT")
            if by and value:
                return by, value, description
        except json.JSONDecodeError:
            LOGGER.debug("Unable to parse ChatGPT response: %s", response.output_text)
        return None

    def heal_locator(self, html: str, locator: HealableLocator) -> Optional[Tuple[str, str, str]]:
        soup = BeautifulSoup(html, "html.parser")
        minimized_html = soup.prettify()[:60_000]
        suggestion = self._ask_chatgpt(minimized_html, locator)
        if suggestion:
            LOGGER.info("ChatGPT suggested locator: %s", suggestion)
        else:
            LOGGER.info("ChatGPT did not provide a new locator for %s", locator)
        return suggestion


class ChatGPTStepHealer:
    """Collects failing step information and submits it to ChatGPT for analysis."""

    def __init__(self, model: str = "gpt-4o-mini", api_key: Optional[str] = None):
        self.api_key = api_key or os.getenv("OPENAI_API_KEY")
        self.model = model
        self._client: Optional[OpenAI] = None
        if self.api_key:
            self._client = OpenAI(api_key=self.api_key)

    @retry(wait=wait_exponential(min=1, max=10), stop=stop_after_attempt(2), reraise=False)
    def heal_step(self, scenario_name: str, step_name: str, error: str, feature_text: str) -> Optional[str]:
        if not self._client:
            LOGGER.info("Step healing skipped because no OpenAI client is configured")
            return None
        system_prompt = (
            "Du bist ein KI-Assistent, der gescheiterte BDD-Schritte analysiert und Lösungsvorschläge "
            "für Testautomatisierung liefert."
        )
        user_prompt = json.dumps(
            {
                "scenario": scenario_name,
                "step": step_name,
                "error": error,
                "feature": feature_text,
                "task": "Beschreibe mögliche Ursachen und schlage konkrete Code- oder Locator-Anpassungen vor."
            },
            ensure_ascii=False,
        )
        response = self._client.responses.create(
            model=self.model,
            input=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
        )
        return response.output_text if response else None
