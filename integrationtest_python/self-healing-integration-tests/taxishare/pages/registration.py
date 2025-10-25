"""Selenium page object for the registration flow."""

from __future__ import annotations

from selenium.webdriver.common.by import By

from ..ui import BasePage


class RegistrationPage(BasePage):
    DEFAULT_PATH = "register"
    PATH_V2 = "registerV2"

    def username_input(self):
        return self.driver.find_element(By.NAME, "username")

    def email_input(self):
        return self.driver.find_element(By.NAME, "email")

    def password_input(self):
        return self.driver.find_element(By.NAME, "password")

    def submit_button(self):
        try:
            return self.driver.find_element(By.CLASS_NAME, "btn-primary")
        except Exception:
            return self.driver.find_element(By.CSS_SELECTOR, "button[type='submit']")

    def alert_success(self):
        return self.driver.find_element(By.CLASS_NAME, "alert-success")

    def register(self, username: str, email: str, password: str) -> None:
        for locator in (self.username_input(), self.email_input(), self.password_input()):
            locator.clear()
        self.username_input().send_keys(username)
        self.email_input().send_keys(email)
        self.password_input().send_keys(password)
        self.submit_button().click()

    def registration_success_message(self) -> str:
        return self.alert_success().text

