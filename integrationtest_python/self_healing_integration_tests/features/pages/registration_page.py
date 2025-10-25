from __future__ import annotations

from dataclasses import dataclass

from selenium.webdriver.common.by import By

from .base_page import BasePage
from ..support.self_healing import HealableLocator


@dataclass
class RegistrationPage(BasePage):
    url_path: str = "register"

    username_input: HealableLocator = HealableLocator(By.NAME, "username", "Registration username")
    email_input: HealableLocator = HealableLocator(By.NAME, "email", "Registration email")
    password_input: HealableLocator = HealableLocator(By.NAME, "password", "Registration password")
    submit_button: HealableLocator = HealableLocator(By.CSS_SELECTOR, "button.btn-primary", "Register button")
    success_banner: HealableLocator = HealableLocator(By.CSS_SELECTOR, "div.alert-success", "Success message")

    def register(self, username: str, email: str, password: str) -> None:
        self.find(self.username_input).send_keys(username)
        self.find(self.email_input).send_keys(email)
        self.find(self.password_input).send_keys(password)
        self.find(self.submit_button).click()

    def success_message(self) -> str:
        return self.find(self.success_banner).text

