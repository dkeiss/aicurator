"""Requests based HTTP client mirroring the Java RestAssured configuration."""

from __future__ import annotations

from typing import Any
from urllib.parse import urljoin

import requests


class HttpClient:
    def __init__(self, base_url: str) -> None:
        self._base_url = base_url.rstrip("/") + "/"
        self._session = requests.Session()
        self._default_headers = {
            "Accept": "application/json",
            "Content-Type": "application/json",
        }

    def request(self, method: str, path: str, **kwargs: Any) -> requests.Response:
        url = urljoin(self._base_url, path.lstrip("/"))
        headers = {**self._default_headers, **kwargs.pop("headers", {})}
        response = self._session.request(method=method, url=url, headers=headers, **kwargs)
        return response

    def get(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("GET", path, **kwargs)

    def post(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("POST", path, **kwargs)

    def put(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("PUT", path, **kwargs)

    def delete(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("DELETE", path, **kwargs)

    def close(self) -> None:
        self._session.close()

