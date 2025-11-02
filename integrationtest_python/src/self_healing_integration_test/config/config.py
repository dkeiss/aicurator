"""Runtime configuration for the TaxiShare Behave suite."""

from __future__ import annotations

from dataclasses import dataclass
import os


def _env(name: str, default: str | None = None) -> str | None:
    value = os.getenv(name, default)
    if value is None:
        return None
    return value.strip()


@dataclass(slots=True)
class Settings:
    frontend_url: str = _env("TAXISHARE_FRONTEND_URL", "http://localhost:3000")
    backend_url: str = _env("TAXISHARE_BACKEND_URL", "http://localhost:8080")
    selenium_remote_url: str | None = _env("SELENIUM_REMOTE_URL")
    selenium_browser: str = _env("SELENIUM_BROWSER", "chrome")
    step_healing_enabled: bool = _env("STEP_HEALING_ENABLED", "false").lower() == "true"
    healing_approach: str = _env("HEALING_APPROACH", "aicurator")

def load_settings() -> Settings:
    """Load settings from the process environment."""

    return Settings()
