"""Utility helpers for reading bundled resources and files."""

from __future__ import annotations

from importlib import resources
from pathlib import Path
from typing import Optional


def read_resource_text(resource_name: str) -> str:
    """Read a text resource bundled with the package.

    Parameters
    ----------
    resource_name:
        Name of the resource file located in ``aicurator.resources``.
    """

    with resources.files("aicurator.resources").joinpath(resource_name).open("r", encoding="utf-8") as handle:
        return handle.read()


def read_file_if_exists(path: Path) -> Optional[str]:
    """Return the contents of *path* if it exists, otherwise ``None``."""

    if path.exists():
        return path.read_text(encoding="utf-8")
    return None
