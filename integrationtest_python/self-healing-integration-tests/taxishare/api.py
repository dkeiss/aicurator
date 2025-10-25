"""High level helpers for interacting with the TaxiShare REST API."""

from __future__ import annotations

from typing import Dict

from faker import Faker

from .data import Reservation, User
from .http import HttpClient
from .store import ScenarioStore

faker = Faker()


def register_new_user(client: HttpClient, store: ScenarioStore) -> User:
    user = User(
        username=faker.user_name(),
        email=faker.email(),
        roles={"user"},
        password=faker.password(),
    )
    payload = {
        "username": user.username,
        "email": user.email,
        "password": user.password,
        "roles": sorted(user.roles),
    }
    response = client.post("/api/auth/register", json=payload)
    response.raise_for_status()
    store.user = user
    store.response = response
    return user


def login_user(client: HttpClient, store: ScenarioStore, user: User | None = None) -> str:
    if user is None:
        if store.user is None:
            raise RuntimeError("No user available in the scenario store")
        user = store.user
    payload = {
        "username": user.username,
        "password": user.password,
    }
    response = client.post("/api/auth/login", json=payload)
    response.raise_for_status()
    body = response.json()
    token = body.get("accessToken")
    if not token:
        raise RuntimeError("Login response did not contain an access token")
    store.jwt_token = token
    store.response = response
    return token


def auth_headers(store: ScenarioStore) -> Dict[str, str]:
    if not store.jwt_token:
        raise RuntimeError("JWT token is not available; call login_user first")
    return {"Authorization": f"Bearer {store.jwt_token}"}


def delete_reservations(client: HttpClient, store: ScenarioStore) -> None:
    response = client.delete("/api/reservations", headers=auth_headers(store))
    response.raise_for_status()
    store.response = response


def create_reservation(client: HttpClient, store: ScenarioStore, reservation: Reservation) -> None:
    payload = {
        "date": reservation.date,
        "departure": reservation.departure,
        "destination": reservation.destination,
        "startTime": reservation.start_time,
    }
    response = client.post("/api/reservations", json=payload, headers=auth_headers(store))
    store.response = response


def join_reservation(client: HttpClient, store: ScenarioStore, reservation_id: int) -> None:
    response = client.put(
        f"/api/reservations/{reservation_id}/join",
        headers=auth_headers(store),
    )
    store.response = response


def search_reservations(client: HttpClient, store: ScenarioStore, params: Dict[str, str]) -> None:
    response = client.get("/api/reservations", params=params, headers=auth_headers(store))
    store.response = response


def subscribe_to_updates(client: HttpClient, store: ScenarioStore, reservation_id: int) -> None:
    response = client.get(
        f"/api/reservations/{reservation_id}/updates",
        headers=auth_headers(store),
        stream=True,
    )
    response.raise_for_status()
    store.reservation_updates = response.iter_lines(decode_unicode=True)
    store.response = response

