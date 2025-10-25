from __future__ import annotations

import logging
import os
from dataclasses import dataclass
from typing import Optional

from selenium import webdriver
from selenium.common.exceptions import WebDriverException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager

LOGGER = logging.getLogger(__name__)


@dataclass
class BrowserManager:
    """Creates and manages Selenium WebDriver instances."""

    headless: bool = True
    driver: Optional[webdriver.Chrome] = None

    def create(self) -> webdriver.Chrome:
        if self.driver:
            return self.driver

        options = Options()
        if self.headless:
            options.add_argument("--headless=new")
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-gpu")
        options.add_argument("--disable-dev-shm-usage")

        binary_location = os.getenv("CHROME_BINARY")
        if binary_location:
            options.binary_location = binary_location

        try:
            service = Service(ChromeDriverManager().install())
            self.driver = webdriver.Chrome(service=service, options=options)
            self.driver.implicitly_wait(2)
        except WebDriverException as exc:
            LOGGER.error("Unable to start Chrome driver: %s", exc, exc_info=True)
            raise
        return self.driver

    def quit(self) -> None:
        if self.driver:
            try:
                self.driver.quit()
            except WebDriverException:
                LOGGER.debug("Failed to quit driver", exc_info=True)
            finally:
                self.driver = None
