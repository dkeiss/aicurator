"""Behave-specific logic for building remediation prompts."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Optional

from behave.model import Scenario, Step
from behave.model_core import Status

from aicurator.config.files_util import read_file_if_exists
from aicurator.config.prompt_client import PromptClient


@dataclass
class StepFailureContext:
    """Container for information derived from a failed Behave step."""

    exception: str
    method_name: str
    source_code_path: Optional[Path]
    source_code: Optional[str]


class StepPromptCreator:
    """Recreates the behaviour of the Java ``StepPromptCreator`` for Behave."""

    def __init__(self) -> None:
        self._prompt_client = PromptClient("step-prompt.st")

    def build_context(self, scenario: Scenario) -> Optional[StepFailureContext]:
        failed_step = next((step for step in scenario.steps if step.status == Status.failed), None)
        if failed_step is None or failed_step.exception is None:
            return None

        exception = repr(failed_step.exception)
        method_name = getattr(failed_step, "name", failed_step.keyword)
        source_path = self._infer_source_path(failed_step)
        source_code = read_file_if_exists(source_path) if source_path else None
        return StepFailureContext(
            exception=exception,
            method_name=method_name,
            source_code_path=source_path,
            source_code=source_code,
        )

    def create_prompt(self, context: StepFailureContext) -> str:
        return self._prompt_client.render(
            {
                "exception": context.exception,
                "method": context.method_name,
                "sourceCode": context.source_code or "",
                "format": "",
            }
        )

    @staticmethod
    def _infer_source_path(step: Step) -> Optional[Path]:
        func = getattr(step, "function", None)
        if func is None:
            return None
        return Path(func.__code__.co_filename)
