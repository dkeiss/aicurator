"""Selenium WebDriver management for the Behave scenarios."""

from __future__ import annotations

import contextlib
from typing import Optional

from selenium import webdriver
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.remote.webdriver import WebDriver

from .config import Settings


class BrowserManager:
    """Lazy WebDriver loader that mimics the Java WebDriverWrapper component."""

    def __init__(self, settings: Settings) -> None:
        self._settings = settings
        self._driver: Optional[WebDriver] = None

    @property
    def driver(self) -> WebDriver:
        if self._driver is None:
            self._driver = self._load_driver()
        return self._driver

    def is_loaded(self) -> bool:
        return self._driver is not None

    def _load_driver(self) -> WebDriver:
        browser = (self._settings.selenium_browser or "chrome").lower()
        if browser == "chrome":
            options = ChromeOptions()
            options.add_argument("--headless=new")
            options.add_argument("--disable-gpu")
            options.add_argument("--window-size=1920,1080")
            if self._settings.selenium_remote_url:
                return webdriver.Remote(command_executor=self._settings.selenium_remote_url, options=options)
            return webdriver.Chrome(options=options)
        if browser == "firefox":
            options = FirefoxOptions()
            options.add_argument("-headless")
            if self._settings.selenium_remote_url:
                return webdriver.Remote(command_executor=self._settings.selenium_remote_url, options=options)
            return webdriver.Firefox(options=options)
        raise ValueError(f"Unsupported browser '{browser}'")

    def quit(self) -> None:
        with contextlib.suppress(Exception):
            if self._driver:
                self._driver.quit()
        self._driver = None

    def delete_all_cookies(self) -> None:
        if self._driver is not None:
            with contextlib.suppress(Exception):
                self._driver.delete_all_cookies()

