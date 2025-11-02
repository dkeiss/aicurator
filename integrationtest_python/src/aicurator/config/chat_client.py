"""ChatGPT client utilities."""

from __future__ import annotations

import os
from dataclasses import dataclass
from typing import List, Mapping, Sequence

from openai import OpenAI
from tenacity import retry, stop_after_attempt, wait_exponential


@dataclass
class ChatMessage:
    role: str
    content: str


class ChatClient:
    """Wrapper around the OpenAI Chat Completions API with retries."""

    def __init__(self, model: str = "gpt-4o-mini", temperature: float = 0.2, api_key: str | None = None) -> None:
        self.model = model
        self.temperature = temperature
        self.api_key = api_key or os.getenv("OPENAI_API_KEY")
        if not self.api_key:
            raise RuntimeError("OPENAI_API_KEY is not set. Set it in the environment before running tests.")
        self._client = OpenAI(api_key=self.api_key)

    @retry(wait=wait_exponential(multiplier=0.5, min=1, max=30), stop=stop_after_attempt(3))
    def complete(self, messages: Sequence[ChatMessage]) -> str:
        payload: List[Mapping[str, str]] = [message.__dict__ for message in messages]
        response = self._client.chat.completions.create(
            model=self.model,
            temperature=self.temperature,
            messages=payload,
        )
        if not response.choices:
            raise RuntimeError("No choices returned from OpenAI chat completion")
        return response.choices[0].message.content or ""
