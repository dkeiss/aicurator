from __future__ import annotations

from dataclasses import dataclass
from typing import Dict

from faker import Faker

faker = Faker()


@dataclass
class User:
    username: str
    email: str
    roles: tuple[str, ...]
    password: str

    def as_payload(self) -> Dict[str, str | tuple[str, ...]]:
        return {
            "username": self.username,
            "email": self.email,
            "roles": list(self.roles),
            "password": self.password,
        }


@dataclass
class Reservation:
    date: str
    departure: str
    destination: str
    startTime: str

    def as_payload(self) -> Dict[str, str]:
        return {
            "date": self.date,
            "departure": self.departure,
            "destination": self.destination,
            "startTime": self.startTime,
        }


def random_user() -> User:
    return User(
        username=faker.user_name(),
        email=faker.email(),
        roles=("user",),
        password=faker.password(length=12),
    )

