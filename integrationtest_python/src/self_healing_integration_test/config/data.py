"""Shared DTO-style data classes used by the Behave steps."""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import List, Optional, Set


@dataclass(slots=True)
class User:
    username: str
    email: str
    roles: Set[str]
    password: str


@dataclass(slots=True)
class Reservation:
    id: Optional[int] = None
    initiator: Optional[str] = None
    date: Optional[str] = None
    departure: Optional[str] = None
    destination: Optional[str] = None
    start_time: Optional[str] = None
    price: Optional[float] = None
    participants: List[str] = field(default_factory=list)
    message: Optional[str] = None

    @classmethod
    def from_payload(cls, payload: dict) -> "Reservation":
        return cls(
            id=payload.get("id"),
            initiator=payload.get("initiator"),
            date=payload.get("date"),
            departure=payload.get("departure"),
            destination=payload.get("destination"),
            start_time=payload.get("startTime"),
            price=float(payload["price"]) if payload.get("price") is not None else None,
            participants=list(payload.get("participants", [])),
            message=payload.get("message"),
        )
