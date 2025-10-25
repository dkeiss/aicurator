from __future__ import annotations

from typing import Optional, Type, TypeVar

from .scenario_store import ScenarioStore
from .self_healing import ChatGPTLocatorHealer

T = TypeVar("T")


def open_page(
    page_cls: Type[T],
    *,
    driver,
    base_url: str,
    store: ScenarioStore,
    healer: Optional[ChatGPTLocatorHealer],
    url_path: Optional[str] = None,
) -> T:
    page = page_cls(driver=driver, base_url=base_url, healer=healer, url_path=url_path or page_cls.url_path)
    page.open()
    page.check_url()
    store.page = page
    return page


def current_page(store: ScenarioStore) -> T:
    return store.page  # type: ignore[return-value]
