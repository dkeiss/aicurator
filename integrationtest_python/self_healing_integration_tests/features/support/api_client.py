from __future__ import annotations

import base64
import logging
from dataclasses import dataclass, field
from typing import Any, Dict, Mapping, Optional
from urllib.parse import urljoin

import requests
from requests import Response, Session

from .scenario_store import ScenarioStore

LOGGER = logging.getLogger(__name__)


@dataclass
class TaxiShareApiClient:
    base_url: str
    store: ScenarioStore
    session: Session = field(default_factory=requests.Session)

    def _headers(self, authenticated: bool = True) -> Dict[str, str]:
        headers = {
            "Accept": "application/json",
            "Content-Type": "application/json",
        }
        if authenticated and self.store.jwt_token:
            headers["Authorization"] = f"Bearer {self.store.jwt_token}"
        return headers

    def post(self, path: str, payload: Mapping[str, Any], authenticated: bool = True) -> Response:
        response = self.session.post(
            urljoin(self.base_url, path),
            headers=self._headers(authenticated),
            json=payload,
        )
        LOGGER.debug("POST %s responded with %s", path, response.status_code)
        self.store.response = response
        return response

    def delete(self, path: str, authenticated: bool = True) -> Response:
        response = self.session.delete(
            urljoin(self.base_url, path),
            headers=self._headers(authenticated),
        )
        LOGGER.debug("DELETE %s responded with %s", path, response.status_code)
        self.store.response = response
        return response

    def put(self, path: str, authenticated: bool = True, params: Optional[Dict[str, Any]] = None) -> Response:
        response = self.session.put(
            urljoin(self.base_url, path),
            headers=self._headers(authenticated),
            params=params,
        )
        LOGGER.debug("PUT %s responded with %s", path, response.status_code)
        self.store.response = response
        return response

    def get(
        self,
        path: str,
        authenticated: bool = True,
        params: Optional[Dict[str, Any]] = None,
        stream: bool = False,
    ) -> Response:
        response = self.session.get(
            urljoin(self.base_url, path),
            headers=self._headers(authenticated),
            params=params,
            stream=stream,
        )
        LOGGER.debug("GET %s responded with %s", path, response.status_code)
        self.store.response = response
        return response

    @staticmethod
    def encode_query(params: Mapping[str, str]) -> Dict[str, str]:
        return {key: base64.b64encode(value.encode()).decode() for key, value in params.items()}

