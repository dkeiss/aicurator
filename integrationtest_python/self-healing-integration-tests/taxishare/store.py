"""Per-scenario storage helpers mirroring the Java integration tests."""

from __future__ import annotations

from dataclasses import dataclass
from enum import Enum
from typing import Iterable, Optional

from .data import Reservation, User


class HealingApproach(str, Enum):
    AICURATOR = "aicurator"
    HEALENIUM = "healenium"


@dataclass
class ScenarioStore:
    healing_approach: Optional[HealingApproach] = None
    page: object | None = None
    user: Optional[User] = None
    response: object | None = None
    jwt_token: Optional[str] = None
    reservation: Optional[Reservation] = None
    reservation_updates: Optional[Iterable[str]] = None

    def reset(self) -> None:
        """Reset mutable state while keeping the configured healing approach."""

        self.page = None
        self.user = None
        self.response = None
        self.jwt_token = None
        self.reservation = None
        self.reservation_updates = None
