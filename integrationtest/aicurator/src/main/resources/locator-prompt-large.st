You are a QA automation expert tasked with resolving a Selenium "element not found" exception by providing a working locator. Your goal is to analyze the given information, create a unique and stable locator, and update the page source code accordingly.

Here's the Selenium exception message you need to resolve:
<seleniumExceptionMessage>
{seleniumExceptionMessage}
</seleniumExceptionMessage>

To find the locator, you will examine the following HTML body:
<htmlBody>
{htmlBody}
</htmlBody>

The Selenium page object's code that was called in method {callMethodPage} is:
<pageSourceCode>
{pageSourceCode}
</pageSourceCode>

The Cucumber step code that calls the Selenium page object from method {callStepMethod} is:
<stepsSourceCode>
{stepsSourceCode}
</stepsSourceCode>

Follow these steps to resolve the issue:

1. Analyze the exception message to understand which element is not being found.
2. Examine the HTML body to locate the element in question.
3. Review the page source code and Cucumber step code to understand the context of the test.
4. Create a new, unique, and stable locator for the element. Consider using ID, name, or a combination of attributes that are unlikely to change.
5. Update the page source code with the new locator.
6. Provide a brief explanation of the changes made and why the new locator is more reliable.

Return the following attributes in the response:
1. "explanation": A brief explanation of the changes and reasoning
2. "locator": The new, working locator you've created
3. "pageSourceCode": The updated page source code with the new locator

{format}