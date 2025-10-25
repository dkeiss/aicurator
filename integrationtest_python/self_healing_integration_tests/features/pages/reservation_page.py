from __future__ import annotations

from dataclasses import dataclass
from typing import List

from selenium.webdriver.common.by import By

from .base_page import BasePage
from ..support.self_healing import HealableLocator


@dataclass
class ReservationPage(BasePage):
    url_path: str = "reservation"

    date_input: HealableLocator = HealableLocator(By.NAME, "date", "Date input")
    departure_input: HealableLocator = HealableLocator(By.NAME, "departure", "Departure input")
    earliest_input: HealableLocator = HealableLocator(By.NAME, "earliestStartTime", "Earliest start input")
    destination_input: HealableLocator = HealableLocator(By.NAME, "destination", "Destination input")
    latest_input: HealableLocator = HealableLocator(By.NAME, "latestStartTime", "Latest start input")
    search_button: HealableLocator = HealableLocator(By.ID, "searchButton", "Search button")
    reserve_button: HealableLocator = HealableLocator(By.ID, "reserveButton", "Reserve button")
    alert_success: HealableLocator = HealableLocator(By.CSS_SELECTOR, ".alert-success", "Success alert")
    alert_info: HealableLocator = HealableLocator(By.CSS_SELECTOR, ".alert-info", "Info alert")
    reservation_table: HealableLocator = HealableLocator(By.CSS_SELECTOR, "div.table-responsive", "Reservation container")

    def search(self, *, date: str, departure: str, destination: str, earliest: str, latest: str) -> None:
        date_element = self.find(self.date_input)
        date_element.clear()
        date_element.send_keys(date)

        departure_element = self.find(self.departure_input)
        departure_element.clear()
        departure_element.send_keys(departure)

        destination_element = self.find(self.destination_input)
        destination_element.clear()
        destination_element.send_keys(destination)

        earliest_element = self.find(self.earliest_input)
        earliest_element.clear()
        earliest_element.send_keys(earliest)

        latest_element = self.find(self.latest_input)
        latest_element.clear()
        latest_element.send_keys(latest)

        self.find(self.search_button).click()

    def reserve(self) -> None:
        self.find(self.reserve_button).click()

    def join(self, reservation_id: int) -> None:
        table = self.find(self.reservation_table)
        table.find_element(By.ID, f"joinButton{reservation_id}").click()

    def reservation_ids(self) -> List[int]:
        table = self.find(self.reservation_table)
        rows = table.find_elements(By.XPATH, ".//tr[@data-reservation-id]")
        return [int(row.get_attribute("data-reservation-id")) for row in rows]

    def reservations_text(self) -> List[str]:
        table = self.find(self.reservation_table)
        rows = table.find_elements(By.TAG_NAME, "tr")
        return [row.text for row in rows if row.text]

    def reserve_button_visible(self) -> bool:
        return self.find(self.reserve_button).is_displayed()

    def success_message(self) -> str:
        return self.find(self.alert_success).text

    def info_message(self) -> str:
        return self.find(self.alert_info).text

