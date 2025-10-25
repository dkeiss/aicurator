# Self-Healing Integration Tests (Python)

Dieses Modul repliziert den Funktionsumfang der vorhandenen Java-Integrationstests in `../integrationtest`
unter Verwendung von **Python**, **Behave** für BDD, **Selenium** für Browser-Automatisierung sowie einer
ChatGPT-gestützten Selbstheilung. Die Test-Suite unterstützt UI- und API-Szenarien für die TaxiShare-Anwendung
und kann über Behave ausgeführt werden.

## Voraussetzungen

- Python 3.10 oder höher
- Google Chrome oder Chromium-basierter Browser
- Ein gültiger `OPENAI_API_KEY` in der Umgebung, falls die Selbstheilung aktiviert werden soll

```bash
python -m venv .venv
source .venv/bin/activate
pip install -e .
```

## Ausführen der Tests

```bash
behave self_healing_integration_tests/features
```

Setze optionale Umgebungsvariablen, um den Host anzupassen:

```bash
export TAXISHARE_FRONTEND_URL="http://localhost:3000"
export TAXISHARE_BACKEND_URL="http://localhost:8080"
```

## Selbstheilung über ChatGPT

Die Selbstheilung kann über Behave-Userdata gesteuert werden (`step_healing_enabled=true|false`).
Fehlgeschlagene Schritte werden analysiert, relevante Informationen extrahiert und an die OpenAI-API
weitergeleitet, um Vorschläge für Korrekturen oder alternative Locator-Strategien zu generieren.

Die Implementierung nutzt die offizielle `openai`-Bibliothek und kann problemlos an alternative Modelle
angepasst werden. Die Vorschläge werden protokolliert und können optional automatisiert angewendet werden.
