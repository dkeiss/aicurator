from __future__ import annotations

from dataclasses import dataclass
from typing import Optional, Any, Iterable


@dataclass
class ScenarioStore:
    """State container that mimics the behaviour of the Java ScenarioStore."""

    healing_enabled: bool = False
    page: Optional[Any] = None
    user: Optional[Any] = None
    response: Optional[Any] = None
    jwt_token: Optional[str] = None
    reservation: Optional[Any] = None
    reservation_updates: Optional[Iterable[str]] = None

    def reset(self) -> None:
        self.healing_enabled = False
        self.page = None
        self.user = None
        self.response = None
        self.jwt_token = None
        self.reservation = None
        self.reservation_updates = None
