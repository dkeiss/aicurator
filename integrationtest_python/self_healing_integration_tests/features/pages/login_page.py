from __future__ import annotations

from dataclasses import dataclass

from selenium.webdriver.common.by import By

from .base_page import BasePage
from ..support.self_healing import HealableLocator


@dataclass
class LoginPage(BasePage):
    url_path: str = "login"

    username_input: HealableLocator = HealableLocator(By.NAME, "username", "Username input field")
    password_input: HealableLocator = HealableLocator(By.NAME, "password", "Password input field")
    submit_button: HealableLocator = HealableLocator(By.CSS_SELECTOR, "button.btn-primary", "Login button")

    def login(self, username: str, password: str) -> None:
        username_element = self.find(self.username_input)
        username_element.clear()
        username_element.send_keys(username)

        password_element = self.find(self.password_input)
        password_element.clear()
        password_element.send_keys(password)

        self.find(self.submit_button).click()

