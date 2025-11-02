"""A lightweight in-memory prompt cache."""

from __future__ import annotations

from dataclasses import dataclass, field
from threading import RLock
from typing import Dict, Optional


@dataclass
class PromptCache:
    """Thread-safe cache for prompts and model answers."""

    _cache: Dict[str, str] = field(default_factory=dict)
    _lock: RLock = field(default_factory=RLock, init=False)

    def get(self, key: str) -> Optional[str]:
        with self._lock:
            return self._cache.get(key)

    def set(self, key: str, value: str) -> None:
        with self._lock:
            self._cache[key] = value

    def clear(self) -> None:
        with self._lock:
            self._cache.clear()
