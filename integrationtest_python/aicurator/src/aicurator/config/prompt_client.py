"""Prompt helper that mirrors the Java implementation."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Dict

from .files_util import read_resource_text


@dataclass
class PromptClient:
    """Loads prompt templates and performs substitution."""

    template_name: str

    def render(self, context: Dict[str, str]) -> str:
        template = read_resource_text(self.template_name)
        class SafeDict(dict):
            def __missing__(self, key: str) -> str:  # type: ignore[override]
                return ""

        return template.format_map(SafeDict(context))
