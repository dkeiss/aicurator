# AiCurator Python Toolkit

This package contains the Python implementation of the AiCurator self-healing toolkit. It is the sibling of the
Java module in `integrationtest/aicurator`, but is designed for Python test suites built with
[Behave](https://behave.readthedocs.io/), [Selenium](https://www.selenium.dev/) and the
[OpenAI Python SDK](https://github.com/openai/openai-python).

## Features

* Step-level failure analysis for Behave features.
* Locator recovery for Selenium driven tests.
* Prompt templates shared with the Java version to ensure the same guidance for the language model.
* ChatGPT powered self-healing using the official OpenAI client with retry handling.

## Repository layout

```
integrationtest_python/
├── src/
│   ├── aicurator/                   # Toolkit code packaged as ``aicurator-python``
│   └── self_healing_integration_test/ # Behave sample suite that consumes the toolkit
```

The Behave suite demonstrates how to integrate the toolkit and serves as a runnable reference when wiring it into your
own projects.

## Installation

Install the editable package from the repository root:

```bash
cd integrationtest_python
pip install -e .
```

Set the `OPENAI_API_KEY` environment variable before running tests so that the ChatGPT integration can authenticate.

## Using the Behave hooks

The Behave example located in `integrationtest_python/src/self_healing_integration_test` exposes the integration points
inside `features/environment.py`. Import the hooks from there or copy the pattern into your own suite once the toolkit is
installed on the `PYTHONPATH`.
