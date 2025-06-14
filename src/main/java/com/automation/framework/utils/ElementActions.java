package com.automation.framework.utils;

import com.automation.framework.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.function.Function;

/**
 * Element Actions - Provides smart element interactions with automatic recovery mechanisms
 * Integrates with Allure for step reporting and includes comprehensive error handling
 * Implements retry logic and intelligent element handling strategies
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class ElementActions {
    
    // Maximum retry attempts for element actions
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 500;
    
    // Prevent instantiation
    private ElementActions() {
        throw new UnsupportedOperationException("ElementActions is a utility class and cannot be instantiated");
    }
    
    /**
     * Performs a smart click with retry and recovery mechanisms
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     */
    @Step("Click on element: {description}")
    public static void smartClick(By locator, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementClickable(locator);
                element.click();
                return true;
            },
            "click",
            description,
            locator
        );
    }
    
    /**
     * Performs a smart click on a WebElement
     * 
     * @param element WebElement to click
     * @param description Description for logging and reporting
     */
    @Step("Click on element: {description}")
    public static void smartClick(WebElement element, String description) {
        executeWithRetry(
            () -> {
                WaitFactory.waitForElementClickable(element, WaitFactory.getDefaultTimeout());
                element.click();
                return true;
            },
            "click",
            description,
            element
        );
    }
    
    /**
     * Performs a JavaScript click as fallback for regular click failures
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     */
    @Step("JavaScript click on element: {description}")
    public static void javascriptClick(By locator, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementPresent(locator);
                WebDriver driver = DriverManager.getDriver();
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                return true;
            },
            "javascript-click",
            description,
            locator
        );
    }
    
    /**
     * Sends text to an element with smart clearing and validation
     * 
     * @param locator Element locator
     * @param text Text to send
     * @param description Description for logging and reporting
     */
    @Step("Send text '{text}' to element: {description}")
    public static void smartSendKeys(By locator, String text, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementVisible(locator);
                element.clear();
                element.sendKeys(text);
                
                // Validate text was entered correctly
                String actualText = element.getAttribute("value");
                if (!text.equals(actualText)) {
                    LogManager.warn("Text validation failed. Expected: '{}', Actual: '{}'", text, actualText);
                    // Try again with JavaScript
                    WebDriver driver = DriverManager.getDriver();
                    ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", element, text);
                }
                
                return true;
            },
            "send-keys",
            description + " with text: " + text,
            locator
        );
    }
    
    /**
     * Gets text from an element with retry mechanism
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     * @return Element text
     */
    @Step("Get text from element: {description}")
    public static String smartGetText(By locator, String description) {
        return executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementVisible(locator);
                String text = element.getText();
                if (text == null || text.trim().isEmpty()) {
                    // Try getting text using JavaScript if regular getText() returns empty
                    WebDriver driver = DriverManager.getDriver();
                    text = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].innerText || arguments[0].textContent;", element);
                }
                LogManager.debug("Retrieved text '{}' from element: {}", text, description);
                return text;
            },
            "get-text",
            description,
            locator
        );
    }
    
    /**
     * Gets attribute value from an element
     * 
     * @param locator Element locator
     * @param attributeName Attribute name
     * @param description Description for logging and reporting
     * @return Attribute value
     */
    @Step("Get attribute '{attributeName}' from element: {description}")
    public static String smartGetAttribute(By locator, String attributeName, String description) {
        return executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementPresent(locator);
                String attributeValue = element.getAttribute(attributeName);
                LogManager.debug("Retrieved attribute '{}' = '{}' from element: {}", attributeName, attributeValue, description);
                return attributeValue;
            },
            "get-attribute",
            description + " (attribute: " + attributeName + ")",
            locator
        );
    }
    
    /**
     * Scrolls to an element to bring it into view
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     */
    @Step("Scroll to element: {description}")
    public static void scrollToElement(By locator, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementPresent(locator);
                WebDriver driver = DriverManager.getDriver();
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                
                // Wait a moment for scroll to complete
                WaitFactory.sleep(300);
                
                LogManager.debug("Scrolled to element: {}", description);
                return true;
            },
            "scroll-to-element",
            description,
            locator
        );
    }
    
    /**
     * Hovers over an element using Actions
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     */
    @Step("Hover over element: {description}")
    public static void smartHover(By locator, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementVisible(locator);
                WebDriver driver = DriverManager.getDriver();
                Actions actions = new Actions(driver);
                actions.moveToElement(element).perform();
                
                LogManager.debug("Hovered over element: {}", description);
                return true;
            },
            "hover",
            description,
            locator
        );
    }
    
    /**
     * Selects option from dropdown by visible text
     * 
     * @param locator Dropdown element locator
     * @param optionText Option text to select
     * @param description Description for logging and reporting
     */
    @Step("Select option '{optionText}' from dropdown: {description}")
    public static void smartSelectByText(By locator, String optionText, String description) {
        executeWithRetry(
            () -> {
                WebElement dropdown = WaitFactory.waitForElementVisible(locator);
                Select select = new Select(dropdown);
                select.selectByVisibleText(optionText);
                
                // Validate selection
                String selectedText = select.getFirstSelectedOption().getText();
                if (!optionText.equals(selectedText)) {
                    throw new RuntimeException("Selection validation failed. Expected: '" + optionText + "', Selected: '" + selectedText + "'");
                }
                
                LogManager.debug("Selected option '{}' from dropdown: {}", optionText, description);
                return true;
            },
            "select-by-text",
            description + " (option: " + optionText + ")",
            locator
        );
    }
    
    /**
     * Selects option from dropdown by value
     * 
     * @param locator Dropdown element locator
     * @param value Option value to select
     * @param description Description for logging and reporting
     */
    @Step("Select option by value '{value}' from dropdown: {description}")
    public static void smartSelectByValue(By locator, String value, String description) {
        executeWithRetry(
            () -> {
                WebElement dropdown = WaitFactory.waitForElementVisible(locator);
                Select select = new Select(dropdown);
                select.selectByValue(value);
                
                LogManager.debug("Selected option by value '{}' from dropdown: {}", value, description);
                return true;
            },
            "select-by-value",
            description + " (value: " + value + ")",
            locator
        );
    }
    
    /**
     * Checks if element is displayed
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     * @return true if element is displayed
     */
    @Step("Check if element is displayed: {description}")
    public static boolean isElementDisplayed(By locator, String description) {
        try {
            WebElement element = WaitFactory.waitForElementPresent(locator, 5); // Short timeout
            boolean displayed = element.isDisplayed();
            LogManager.debug("Element '{}' is displayed: {}", description, displayed);
            return displayed;
        } catch (Exception e) {
            LogManager.debug("Element '{}' is not displayed: {}", description, e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if element exists in DOM
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     * @return true if element exists
     */
    @Step("Check if element exists: {description}")
    public static boolean isElementPresent(By locator, String description) {
        try {
            WaitFactory.waitForElementPresent(locator, 2); // Very short timeout
            LogManager.debug("Element '{}' is present in DOM", description);
            return true;
        } catch (Exception e) {
            LogManager.debug("Element '{}' is not present in DOM: {}", description, e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets all elements matching the locator
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     * @return List of WebElements
     */
    @Step("Find all elements: {description}")
    public static List<WebElement> findElements(By locator, String description) {
        return executeWithRetry(
            () -> {
                List<WebElement> elements = WaitFactory.waitForElementsPresent(locator, WaitFactory.getDefaultTimeout());
                LogManager.debug("Found {} elements for: {}", elements.size(), description);
                return elements;
            },
            "find-elements",
            description,
            locator
        );
    }
    
    /**
     * Double clicks on an element
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     */
    @Step("Double click on element: {description}")
    public static void smartDoubleClick(By locator, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementClickable(locator);
                WebDriver driver = DriverManager.getDriver();
                Actions actions = new Actions(driver);
                actions.doubleClick(element).perform();
                
                LogManager.debug("Double clicked on element: {}", description);
                return true;
            },
            "double-click",
            description,
            locator
        );
    }
    
    /**
     * Right clicks on an element
     * 
     * @param locator Element locator
     * @param description Description for logging and reporting
     */
    @Step("Right click on element: {description}")
    public static void smartRightClick(By locator, String description) {
        executeWithRetry(
            () -> {
                WebElement element = WaitFactory.waitForElementClickable(locator);
                WebDriver driver = DriverManager.getDriver();
                Actions actions = new Actions(driver);
                actions.contextClick(element).perform();
                
                LogManager.debug("Right clicked on element: {}", description);
                return true;
            },
            "right-click",
            description,
            locator
        );
    }
    
    /**
     * Executes an action with retry mechanism and error recovery
     * 
     * @param action The action to execute
     * @param actionType Type of action for logging
     * @param description Description of the element/action
     * @param locator Element locator for error reporting
     * @param <T> Return type of the action
     * @return Result of the action
     */
    private static <T> T executeWithRetry(ActionFunction<T> action, String actionType, String description, Object locator) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                LogManager.debug("Executing {} action for '{}' (attempt {}/{})", actionType, description, attempt, MAX_RETRY_ATTEMPTS);
                
                T result = action.execute();
                
                if (attempt > 1) {
                    LogManager.info("Action '{}' succeeded on attempt {} for: {}", actionType, attempt, description);
                }
                
                return result;
                
            } catch (Exception e) {
                lastException = e;
                LogManager.warn("Action '{}' failed on attempt {} for '{}': {}", actionType, attempt, description, e.getMessage());
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    // Try recovery strategies before retrying
                    tryRecoveryStrategies(actionType, description, attempt);
                    
                    // Wait before retry
                    WaitFactory.sleep(RETRY_DELAY_MS);
                } else {
                    // Capture screenshot on final failure
                    try {
                        ScreenshotUtil.captureFailureScreenshot(description + "_" + actionType + "_failed");
                    } catch (Exception screenshotException) {
                        LogManager.warn("Failed to capture failure screenshot: {}", screenshotException.getMessage());
                    }
                }
            }
        }
        
        LogManager.error("Action '{}' failed after {} attempts for: {}", actionType, MAX_RETRY_ATTEMPTS, description);
        throw new RuntimeException("Element action failed after " + MAX_RETRY_ATTEMPTS + " attempts: " + actionType + " on " + description, lastException);
    }
    
    /**
     * Attempts recovery strategies based on the type of action and failure
     * 
     * @param actionType Type of action that failed
     * @param description Description of the element/action
     * @param attemptNumber Current attempt number
     */
    private static void tryRecoveryStrategies(String actionType, String description, int attemptNumber) {
        LogManager.debug("Attempting recovery strategies for '{}' action on attempt {}", actionType, attemptNumber);
        
        try {
            // Strategy 1: Refresh driver state
            if (attemptNumber == 1) {
                // Check if driver session is still active
                if (!DriverManager.isDriverSessionActive()) {
                    LogManager.warn("Driver session appears inactive, cannot recover");
                    return;
                }
            }
            
            // Strategy 2: Scroll to top to reset page state
            if (attemptNumber == 2) {
                WebDriver driver = DriverManager.getDriver();
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
                LogManager.debug("Scrolled to top of page for recovery");
            }
            
            // Strategy 3: Clear any overlay or modal that might be blocking interaction
            if (attemptNumber >= 2) {
                WebDriver driver = DriverManager.getDriver();
                // Try to close any modals or overlays
                ((JavascriptExecutor) driver).executeScript(
                    "var modals = document.querySelectorAll('.modal, .overlay, .popup'); " +
                    "modals.forEach(function(modal) { modal.style.display = 'none'; });"
                );
                LogManager.debug("Attempted to close modals/overlays for recovery");
            }
            
        } catch (Exception recoveryException) {
            LogManager.debug("Recovery strategy failed: {}", recoveryException.getMessage());
        }
    }
    
    /**
     * Functional interface for action execution
     * 
     * @param <T> Return type of the action
     */
    @FunctionalInterface
    private interface ActionFunction<T> {
        T execute() throws Exception;
    }
} 