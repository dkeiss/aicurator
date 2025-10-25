"""Utilities shared between the Behave UI steps."""

from __future__ import annotations

import inspect
from typing import Optional, Type, TypeVar
from urllib.parse import urljoin

from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.support.wait import WebDriverWait

from .browser import BrowserManager
from .config import Settings
from .store import ScenarioStore

TPage = TypeVar("TPage", bound="BasePage")


class BasePage:
    """Simplified analogue to the Java `AbstractPage`."""

    DEFAULT_PATH: str = ""

    def __init__(self, driver: WebDriver) -> None:
        self.driver = driver

    def wait_for_load(self, url_fragment: str) -> None:
        wait = WebDriverWait(self.driver, 15)
        wait.until(lambda drv: url_fragment in drv.current_url)
        if hasattr(self.driver, "execute_script"):
            wait.until(lambda drv: drv.execute_script("return document.readyState") == "complete")


def get_browser(context) -> WebDriver:
    browser_manager: BrowserManager = context.browser_manager
    browser = browser_manager.driver
    context.browser = browser
    return browser


def open_url(context, url: str) -> None:
    browser = get_browser(context)
    browser.get(url)


def open_relative(context, settings: Settings, path: str) -> None:
    base = settings.frontend_url.rstrip("/") + "/"
    open_url(context, urljoin(base, path))


def expect_page(context, store: ScenarioStore, page_cls: Type[TPage], url_fragment: Optional[str] = None) -> TPage:
    browser = get_browser(context)
    page = page_cls(browser)
    fragment = url_fragment or page_cls.DEFAULT_PATH
    if fragment:
        page.wait_for_load(fragment)
    store.page = page
    context.page_object_source = inspect.getsource(page_cls)
    context.page_object_method = f"{page_cls.__module__}.{page_cls.__name__}"
    return page


def current_page(store: ScenarioStore, page_cls: Type[TPage]) -> TPage:
    page = store.page
    if isinstance(page, page_cls):
        return page
    raise RuntimeError(f"Expected current page to be {page_cls.__name__}, got {type(page).__name__ if page else 'None'}")


def call_page_method(context, page: BasePage, method_name: str, *args, **kwargs):
    method = getattr(page, method_name)
    context.page_object_source = inspect.getsource(page.__class__)
    context.page_object_method = f"{page.__class__.__module__}.{page.__class__.__name__}.{method_name}"
    return method(*args, **kwargs)

