"""Selenium page object for the login screen."""

from __future__ import annotations

from selenium.webdriver.common.by import By

from self_healing_integration_test.pages.basepage import BasePage


class LoginPage(BasePage):
    DEFAULT_PATH = "login"
    PATH_V2 = "loginV2"
    PATH_V3 = "loginV3"
    PATH_V4 = "loginV4"

    def username_input(self):
        return self.driver.find_element(By.NAME, "username")

    def password_input(self):
        return self.driver.find_element(By.NAME, "password")

    def submit_button(self):
        try:
            return self.driver.find_element(By.CLASS_NAME, "btn-primary")
        except Exception:
            return self.driver.find_element(By.CSS_SELECTOR, "button[type='submit']")

    def login(self, username: str, password: str) -> None:
        field = None
        try:
            field = self.driver.find_element(By.NAME, "username")
        except Exception:
            field = self.driver.find_element(By.NAME, "email")
        field.clear()
        field.send_keys(username)
        password_field = self.driver.find_element(By.NAME, "password")
        password_field.clear()
        password_field.send_keys(password)
        self.submit_button().click()

