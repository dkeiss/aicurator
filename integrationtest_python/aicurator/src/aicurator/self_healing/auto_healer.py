"""Self-healing orchestration for Behave/Selenium tests."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Optional

from behave.model import Scenario

from ..bdd.step_prompt_creator import StepPromptCreator, StepFailureContext
from ..config.chat_client import ChatClient, ChatMessage
from ..config.prompt_cache import PromptCache
from ..locators.locator_prompt_creator import LocatorPromptCreator, LocatorContext


@dataclass
class HealingResult:
    prompt: str
    response: str
    cache_key: str


class AutoHealer:
    """High level entry point used inside Behave environment hooks."""

    def __init__(
        self,
        chat_client: Optional[ChatClient] = None,
        prompt_cache: Optional[PromptCache] = None,
        step_prompt_creator: Optional[StepPromptCreator] = None,
        locator_prompt_creator: Optional[LocatorPromptCreator] = None,
    ) -> None:
        self._chat_client = chat_client
        self._prompt_cache = prompt_cache or PromptCache()
        self._step_prompt_creator = step_prompt_creator or StepPromptCreator()
        self._locator_prompt_creator = locator_prompt_creator or LocatorPromptCreator()

    def handle_failure(self, behave_context, scenario: Scenario) -> Optional[HealingResult]:
        step_context = self._step_prompt_creator.build_context(scenario)
        if not step_context:
            return None

        cache_key = f"step::{step_context.method_name}"
        prompt = self._step_prompt_creator.create_prompt(step_context)

        if self._is_locator_issue(step_context):
            locator_context = self._build_locator_context(behave_context, step_context)
            prompt = self._locator_prompt_creator.create_prompt(locator_context)
            cache_key = f"locator::{step_context.method_name}"

        cached = self._prompt_cache.get(cache_key)
        if cached:
            return HealingResult(prompt=prompt, response=cached, cache_key=cache_key)

        response = self._complete(prompt)
        self._prompt_cache.set(cache_key, response)
        return HealingResult(prompt=prompt, response=response, cache_key=cache_key)

    def _complete(self, prompt: str) -> str:
        client = self._chat_client or ChatClient()
        messages = [
            ChatMessage(role="system", content="You are a senior QA engineer helping to heal Behave/Selenium tests."),
            ChatMessage(role="user", content=prompt),
        ]
        return client.complete(messages)

    @staticmethod
    def _is_locator_issue(step_context: StepFailureContext) -> bool:
        return "NoSuchElementException" in step_context.exception

    @staticmethod
    def _build_locator_context(behave_context, step_context: StepFailureContext) -> LocatorContext:
        browser = getattr(behave_context, "browser", None)
        html_body = getattr(browser, "page_source", "") if browser else ""
        page_source_code = getattr(behave_context, "page_object_source", "")
        call_method_page = getattr(behave_context, "page_object_method", step_context.method_name)
        steps_source_code = step_context.source_code or ""
        call_step_method = step_context.method_name
        large = len(html_body) > 6000 or len(page_source_code) > 4000
        return LocatorContext(
            selenium_exception_message=step_context.exception,
            html_body=html_body,
            page_source_code=page_source_code,
            call_method_page=call_method_page,
            steps_source_code=steps_source_code,
            call_step_method=call_step_method,
            large=large,
        )
