from __future__ import annotations

from behave import given

from ..support.data_factory import User, random_user


def register_user_via_api(context, persist: bool = True) -> User:
    user = random_user()
    response = context.api_client.post("/api/auth/register", user.as_payload(), authenticated=False)
    if response.status_code != 200:
        raise AssertionError(f"Registration failed: {response.status_code} {response.text}")
    if persist:
        context.store.user = user
    return user


@given("a new registered user")
def step_new_registered_user(context) -> None:
    register_user_via_api(context, persist=True)
