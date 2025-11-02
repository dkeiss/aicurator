"""Selenium page object for the reservation UI."""

from __future__ import annotations

from selenium.webdriver.common.by import By

from self_healing_integration_test.pages.basepage import BasePage


class ReservationPage(BasePage):
    DEFAULT_PATH = "reservation"
    PATH_V2 = "reservationV2"

    def _input(self, name: str):
        return self.driver.find_element(By.NAME, name)

    def _button_by_id(self, element_id: str):
        return self.driver.find_element(By.ID, element_id)

    def set_date(self, value: str) -> None:
        element = self._input("date")
        element.clear()
        element.send_keys(value)

    def set_departure(self, value: str) -> None:
        element = self._input("departure")
        element.clear()
        element.send_keys(value)

    def set_earliest_start_time(self, value: str) -> None:
        element = self._input("earliestStartTime")
        element.clear()
        element.send_keys(value)

    def set_destination(self, value: str) -> None:
        element = self._input("destination")
        element.clear()
        element.send_keys(value)

    def set_latest_start_time(self, value: str) -> None:
        element = self._input("latestStartTime")
        element.clear()
        element.send_keys(value)

    def submit_search(self) -> None:
        try:
            button = self._button_by_id("searchButton")
        except Exception:
            button = self.driver.find_element(By.CSS_SELECTOR, "button[type='submit'][name='search']")
        button.click()

    def submit_reservation(self) -> None:
        try:
            button = self._button_by_id("reserveButton")
        except Exception:
            button = self.driver.find_element(By.CSS_SELECTOR, "button[type='button'][name='reserve']")
        button.click()

    def reserve_button_is_displayed(self) -> bool:
        try:
            button = self._button_by_id("reserveButton")
        except Exception:
            button = self.driver.find_element(By.CSS_SELECTOR, "button[name='reserve']")
        return button.is_displayed()

    def alert_success_message(self) -> str:
        return self.driver.find_element(By.CLASS_NAME, "alert-success").text

    def alert_info_message(self) -> str:
        return self.driver.find_element(By.CLASS_NAME, "alert-info").text

    def reservation_container(self):
        return self.driver.find_element(By.CLASS_NAME, "table-responsive")

    def reservation_id(self, index: int) -> str:
        container = self.reservation_container()
        row = container.find_element(By.XPATH, f"(//tr[@data-reservation-id])[{index + 1}]")
        return row.get_attribute("data-reservation-id")

    def reservations(self):
        container = self.reservation_container()
        return [row.text for row in container.find_elements(By.TAG_NAME, "tr")]

    def join_button_exists(self, reservation_id: int) -> bool:
        container = self.reservation_container()
        buttons = container.find_elements(By.ID, f"joinButton{reservation_id}")
        if buttons:
            return True
        buttons = container.find_elements(By.CSS_SELECTOR, f"button[name='join'][data-reservation-id='{reservation_id}']")
        return bool(buttons)

    def click_join_button(self, reservation_id: int) -> None:
        container = self.reservation_container()
        try:
            button = container.find_element(By.ID, f"joinButton{reservation_id}")
        except Exception:
            button = container.find_element(By.CSS_SELECTOR, f"button[name='join'][data-reservation-id='{reservation_id}']")
        button.click()

