from __future__ import annotations

import logging
import time
from dataclasses import dataclass
from typing import Optional

from bs4 import BeautifulSoup
from selenium.common.exceptions import NoSuchElementException

from ..support.self_healing import ChatGPTLocatorHealer, HealableLocator

LOGGER = logging.getLogger(__name__)


@dataclass
class BasePage:
    driver: any
    base_url: str
    healer: Optional[ChatGPTLocatorHealer] = None
    url_path: str = ""

    def open(self) -> None:
        full_url = self.base_url.rstrip("/") + "/" + self.url_path.lstrip("/")
        LOGGER.debug("Opening %s", full_url)
        self.driver.get(full_url)
        self.wait_for_ready_state()

    def wait_for_ready_state(self, timeout: float = 15.0) -> None:
        end_time = time.time() + timeout
        while time.time() < end_time:
            state = self.driver.execute_script("return document.readyState")
            if state == "complete":
                return
            time.sleep(0.2)
        LOGGER.warning("Timed out waiting for ready state")

    def check_url(self) -> None:
        if self.url_path and self.url_path not in self.driver.current_url:
            raise AssertionError(
                f"Expected '{self.url_path}' to be part of {self.driver.current_url}"
            )

    def find(self, locator: HealableLocator) -> any:
        try:
            return locator.locate(self.driver)
        except NoSuchElementException as exc:
            LOGGER.debug("Locator %s failed: %s", locator, exc, exc_info=True)
            if not self.healer:
                raise
            html = self.driver.page_source
            suggestion = self.healer.heal_locator(html, locator)
            if suggestion:
                LOGGER.info("Applying healed locator %s -> %s", locator, suggestion)
                healed_locator = HealableLocator(*suggestion)
                return healed_locator.locate(self.driver)
            raise

    def page_source_dom(self) -> BeautifulSoup:
        return BeautifulSoup(self.driver.page_source, "html.parser")

