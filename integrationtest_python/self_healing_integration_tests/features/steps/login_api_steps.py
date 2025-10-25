from __future__ import annotations

from behave import given

from ..support.data_factory import User
from .registration_api_steps import register_user_via_api


def login_user_via_api(context, user: User | None = None) -> None:
    user = user or context.store.user
    if user is None:
        user = register_user_via_api(context)
    response = context.api_client.post("/api/auth/login", user.as_payload(), authenticated=False)
    if response.status_code != 200:
        raise AssertionError(f"Login failed: {response.status_code} {response.text}")
    body = response.json()
    context.store.jwt_token = body.get("accessToken")
    if not context.store.jwt_token:
        raise AssertionError("Login response does not contain accessToken")


@given("a logged-in customer via API")
def step_logged_in_customer_via_api(context) -> None:
    user = context.store.user if isinstance(context.store.user, User) else None
    if user is None:
        user = register_user_via_api(context)
    login_user_via_api(context, user)
