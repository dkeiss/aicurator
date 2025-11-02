"""Prompt creation for Selenium locator self healing."""

from __future__ import annotations

from dataclasses import dataclass

from aicurator.config.prompt_client import PromptClient


@dataclass
class LocatorContext:
    selenium_exception_message: str
    html_body: str
    page_source_code: str
    call_method_page: str
    steps_source_code: str
    call_step_method: str
    large: bool = False


class LocatorPromptCreator:
    def __init__(self) -> None:
        self._default_prompt = PromptClient("locator-prompt.st")
        self._large_prompt = PromptClient("locator-prompt-large.st")

    def create_prompt(self, context: LocatorContext) -> str:
        prompt = self._large_prompt if context.large else self._default_prompt
        return prompt.render(
            {
                "seleniumExceptionMessage": context.selenium_exception_message,
                "htmlBody": context.html_body,
                "pageSourceCode": context.page_source_code,
                "callMethodPage": context.call_method_page,
                "stepsSourceCode": context.steps_source_code,
                "callStepMethod": context.call_step_method,
                "format": "",
            }
        )
