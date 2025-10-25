# AiCurator Python Toolkit

This module provides the Python implementation of the AiCurator self-healing toolkit. It mirrors the Maven-based module
from `integrationtest/aicurator` but is built on top of Python, [Behave](https://behave.readthedocs.io/), [Selenium](https://www.selenium.dev/),
and the [OpenAI Python SDK](https://github.com/openai/openai-python).

## Features

* Step-level failure analysis for Behave features.
* Locator recovery for Selenium driven tests.
* Prompt templates shared with the Java version to ensure the same guidance for the language model.
* ChatGPT powered self-healing using the official OpenAI client with retry handling.

## Installation

```bash
pip install -e .
```

Set the `OPENAI_API_KEY` environment variable before running the tests so that the ChatGPT integration can authenticate.

## Running Behave with self-healing hooks

Add the module to the `PYTHONPATH` (the editable install above already does that) and configure the Behave
`environment.py` hooks as shown in `integrationtest_python/self-healing-integration-tests`.
