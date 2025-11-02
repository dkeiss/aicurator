# Self-healing integration tests (Python)

This package mirrors `self_healing_integration_test/self-healing-integration-tests` but is implemented with Python and Behave.
It demonstrates how to wire the `aicurator-python` toolkit into Behave hooks so that failing steps are analysed
and potential fixes are requested from ChatGPT. The feature set matches the Java suite: login, registration,
web-driven reservation flows, and the supporting REST API checks have all been ported to Behave.

## Usage

1. Install both packages in editable mode:
   ```bash
   pip install -e ../aicurator
   pip install -e .
   ```
2. Export your `OPENAI_API_KEY`.
3. Ensure the TaxiShare sample application is reachable (defaults: frontend `http://localhost:3000`, backend `http://localhost:8080`).
   Override via `TAXISHARE_FRONTEND_URL` and `TAXISHARE_BACKEND_URL` if necessary. Selenium settings can be controlled with
   `SELENIUM_BROWSER` (``chrome``/``firefox``) and `SELENIUM_REMOTE_URL` when driving a remote grid.
4. Run the feature suite:
   ```bash
   behave
   ```

Failures will automatically trigger the self-healing logic defined in `features/environment.py`.
