package com.automation.framework.utils;

import com.automation.framework.config.ConfigLoader;
import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Wait Factory - Provides intelligent wait strategies using FluentWait and ExpectedConditions
 * Implements adaptive wait patterns with automatic timeout adjustment and exception filtering
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class WaitFactory {
    
    private static final ConfigLoader config = ConfigLoader.getInstance();
    
    // Prevent instantiation
    private WaitFactory() {
        throw new UnsupportedOperationException("WaitFactory is a utility class and cannot be instantiated");
    }
    
    /**
     * Creates a standard WebDriverWait instance
     * 
     * @param timeoutInSeconds The timeout in seconds
     * @return WebDriverWait instance
     */
    public static WebDriverWait createWebDriverWait(int timeoutInSeconds) {
        return new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutInSeconds));
    }
    
    /**
     * Creates a FluentWait instance with standard configuration
     * 
     * @param timeoutInSeconds The timeout in seconds
     * @param pollingIntervalInSeconds The polling interval in seconds
     * @return FluentWait instance
     */
    public static Wait<WebDriver> createFluentWait(int timeoutInSeconds, int pollingIntervalInSeconds) {
        return new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofSeconds(pollingIntervalInSeconds))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }
    
    /**
     * Creates a FluentWait instance with custom exception handling
     * 
     * @param timeoutInSeconds The timeout in seconds
     * @param pollingIntervalInSeconds The polling interval in seconds
     * @param exceptionsToIgnore List of exception classes to ignore
     * @return FluentWait instance
     */
    @SafeVarargs
    public static Wait<WebDriver> createFluentWait(int timeoutInSeconds, int pollingIntervalInSeconds, 
                                                   Class<? extends Throwable>... exceptionsToIgnore) {
        FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofSeconds(pollingIntervalInSeconds));
        
        for (Class<? extends Throwable> exception : exceptionsToIgnore) {
            wait = wait.ignoring(exception);
        }
        
        return wait;
    }
    
    // ========== ELEMENT VISIBILITY WAITS ==========
    
    /**
     * Waits for an element to be visible using the default timeout
     * 
     * @param locator The By locator for the element
     * @return The visible WebElement
     */
    public static WebElement waitForElementVisible(By locator) {
        return waitForElementVisible(locator, config.getExplicitWaitTimeout());
    }
    
    /**
     * Waits for an element to be visible with custom timeout
     * 
     * @param locator The By locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return The visible WebElement
     */
    public static WebElement waitForElementVisible(By locator, int timeoutInSeconds) {
        LogManager.debug("Waiting for element to be visible: {}", locator);
        
        try {
            WebElement element = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            
            LogManager.logElementFound(locator.toString());
            return element;
            
        } catch (TimeoutException e) {
            LogManager.logTimeout(locator.toString());
            throw new TimeoutException("Element not visible within " + timeoutInSeconds + 
                " seconds: " + locator, e);
        }
    }
    
    /**
     * Waits for an element to be visible using FluentWait
     * 
     * @param locator The By locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @param pollingIntervalInSeconds The polling interval in seconds
     * @return The visible WebElement
     */
    public static WebElement waitForElementVisibleFluent(By locator, int timeoutInSeconds, int pollingIntervalInSeconds) {
        LogManager.debug("Waiting for element to be visible (FluentWait): {}", locator);
        
        try {
            WebElement element = createFluentWait(timeoutInSeconds, pollingIntervalInSeconds)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            
            LogManager.logElementFound(locator.toString());
            return element;
            
        } catch (TimeoutException e) {
            LogManager.logTimeout(locator.toString());
            throw new TimeoutException("Element not visible within " + timeoutInSeconds + 
                " seconds (FluentWait): " + locator, e);
        }
    }
    
    // ========== ELEMENT CLICKABILITY WAITS ==========
    
    /**
     * Waits for an element to be clickable using the default timeout
     * 
     * @param locator The By locator for the element
     * @return The clickable WebElement
     */
    public static WebElement waitForElementClickable(By locator) {
        return waitForElementClickable(locator, config.getExplicitWaitTimeout());
    }
    
    /**
     * Waits for an element to be clickable with custom timeout
     * 
     * @param locator The By locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return The clickable WebElement
     */
    public static WebElement waitForElementClickable(By locator, int timeoutInSeconds) {
        LogManager.debug("Waiting for element to be clickable: {}", locator);
        
        try {
            WebElement element = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.elementToBeClickable(locator));
            
            LogManager.logElementFound(locator.toString());
            return element;
            
        } catch (TimeoutException e) {
            LogManager.logTimeout(locator.toString());
            throw new TimeoutException("Element not clickable within " + timeoutInSeconds + 
                " seconds: " + locator, e);
        }
    }
    
    /**
     * Waits for a WebElement to be clickable
     * 
     * @param element The WebElement to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @return The clickable WebElement
     */
    public static WebElement waitForElementClickable(WebElement element, int timeoutInSeconds) {
        LogManager.debug("Waiting for WebElement to be clickable");
        
        try {
            WebElement clickableElement = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.elementToBeClickable(element));
            
            LogManager.debug("WebElement is now clickable");
            return clickableElement;
            
        } catch (TimeoutException e) {
            LogManager.error("WebElement not clickable within {} seconds", timeoutInSeconds);
            throw new TimeoutException("WebElement not clickable within " + timeoutInSeconds + " seconds", e);
        }
    }
    
    // ========== ELEMENT PRESENCE WAITS ==========
    
    /**
     * Waits for an element to be present in DOM using the default timeout
     * 
     * @param locator The By locator for the element
     * @return The present WebElement
     */
    public static WebElement waitForElementPresent(By locator) {
        return waitForElementPresent(locator, config.getExplicitWaitTimeout());
    }
    
    /**
     * Waits for an element to be present in DOM with custom timeout
     * 
     * @param locator The By locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return The present WebElement
     */
    public static WebElement waitForElementPresent(By locator, int timeoutInSeconds) {
        LogManager.debug("Waiting for element to be present: {}", locator);
        
        try {
            WebElement element = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
            
            LogManager.logElementFound(locator.toString());
            return element;
            
        } catch (TimeoutException e) {
            LogManager.logTimeout(locator.toString());
            throw new TimeoutException("Element not present within " + timeoutInSeconds + 
                " seconds: " + locator, e);
        }
    }
    
    /**
     * Waits for all elements matching locator to be present
     * 
     * @param locator The By locator for the elements
     * @param timeoutInSeconds The timeout in seconds
     * @return List of present WebElements
     */
    public static List<WebElement> waitForElementsPresent(By locator, int timeoutInSeconds) {
        LogManager.debug("Waiting for elements to be present: {}", locator);
        
        try {
            List<WebElement> elements = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            
            LogManager.debug("Found {} elements matching locator: {}", elements.size(), locator);
            return elements;
            
        } catch (TimeoutException e) {
            LogManager.logTimeout(locator.toString());
            throw new TimeoutException("Elements not present within " + timeoutInSeconds + 
                " seconds: " + locator, e);
        }
    }
    
    // ========== TEXT AND ATTRIBUTE WAITS ==========
    
    /**
     * Waits for an element to contain specific text
     * 
     * @param locator The By locator for the element
     * @param text The text to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @return true if text is found
     */
    public static boolean waitForTextToBePresent(By locator, String text, int timeoutInSeconds) {
        LogManager.debug("Waiting for text '{}' to be present in element: {}", text, locator);
        
        try {
            boolean result = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            
            LogManager.debug("Text '{}' found in element: {}", text, locator);
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("Text '{}' not found in element {} within {} seconds", text, locator, timeoutInSeconds);
            throw new TimeoutException("Text '" + text + "' not found in element within " + 
                timeoutInSeconds + " seconds: " + locator, e);
        }
    }
    
    /**
     * Waits for an element's attribute to have a specific value
     * 
     * @param locator The By locator for the element
     * @param attribute The attribute name
     * @param value The expected attribute value
     * @param timeoutInSeconds The timeout in seconds
     * @return true if attribute has expected value
     */
    public static boolean waitForAttributeValue(By locator, String attribute, String value, int timeoutInSeconds) {
        LogManager.debug("Waiting for attribute '{}' to have value '{}' in element: {}", attribute, value, locator);
        
        try {
            boolean result = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.attributeToBe(locator, attribute, value));
            
            LogManager.debug("Attribute '{}' has expected value '{}' in element: {}", attribute, value, locator);
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("Attribute '{}' does not have expected value '{}' in element {} within {} seconds", 
                attribute, value, locator, timeoutInSeconds);
            throw new TimeoutException("Attribute '" + attribute + "' does not have expected value '" + 
                value + "' within " + timeoutInSeconds + " seconds: " + locator, e);
        }
    }
    
    // ========== INVISIBILITY WAITS ==========
    
    /**
     * Waits for an element to become invisible
     * 
     * @param locator The By locator for the element
     * @param timeoutInSeconds The timeout in seconds
     * @return true if element becomes invisible
     */
    public static boolean waitForElementInvisible(By locator, int timeoutInSeconds) {
        LogManager.debug("Waiting for element to become invisible: {}", locator);
        
        try {
            boolean result = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
            
            LogManager.debug("Element became invisible: {}", locator);
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("Element did not become invisible within {} seconds: {}", timeoutInSeconds, locator);
            throw new TimeoutException("Element did not become invisible within " + 
                timeoutInSeconds + " seconds: " + locator, e);
        }
    }
    
    // ========== URL AND TITLE WAITS ==========
    
    /**
     * Waits for the page URL to contain specific text
     * 
     * @param urlFragment The URL fragment to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @return true if URL contains the fragment
     */
    public static boolean waitForUrlContains(String urlFragment, int timeoutInSeconds) {
        LogManager.debug("Waiting for URL to contain: {}", urlFragment);
        
        try {
            boolean result = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.urlContains(urlFragment));
            
            LogManager.debug("URL now contains: {}", urlFragment);
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("URL does not contain '{}' within {} seconds", urlFragment, timeoutInSeconds);
            throw new TimeoutException("URL does not contain '" + urlFragment + 
                "' within " + timeoutInSeconds + " seconds", e);
        }
    }
    
    /**
     * Waits for the page title to contain specific text
     * 
     * @param titleFragment The title fragment to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @return true if title contains the fragment
     */
    public static boolean waitForTitleContains(String titleFragment, int timeoutInSeconds) {
        LogManager.debug("Waiting for title to contain: {}", titleFragment);
        
        try {
            boolean result = createWebDriverWait(timeoutInSeconds)
                    .until(ExpectedConditions.titleContains(titleFragment));
            
            LogManager.debug("Title now contains: {}", titleFragment);
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("Title does not contain '{}' within {} seconds", titleFragment, timeoutInSeconds);
            throw new TimeoutException("Title does not contain '" + titleFragment + 
                "' within " + timeoutInSeconds + " seconds", e);
        }
    }
    
    // ========== CUSTOM CONDITION WAITS ==========
    
    /**
     * Waits for a custom condition using FluentWait
     * 
     * @param condition The custom condition to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @param pollingIntervalInSeconds The polling interval in seconds
     * @param <T> The return type of the condition
     * @return The result of the condition
     */
    public static <T> T waitForCondition(Function<WebDriver, T> condition, int timeoutInSeconds, int pollingIntervalInSeconds) {
        LogManager.debug("Waiting for custom condition with timeout: {}s, polling: {}s", timeoutInSeconds, pollingIntervalInSeconds);
        
        try {
            T result = createFluentWait(timeoutInSeconds, pollingIntervalInSeconds)
                    .until(condition);
            
            LogManager.debug("Custom condition satisfied");
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("Custom condition not satisfied within {} seconds", timeoutInSeconds);
            throw new TimeoutException("Custom condition not satisfied within " + timeoutInSeconds + " seconds", e);
        }
    }
    
    /**
     * Waits for a custom ExpectedCondition
     * 
     * @param condition The ExpectedCondition to wait for
     * @param timeoutInSeconds The timeout in seconds
     * @param <T> The return type of the condition
     * @return The result of the condition
     */
    public static <T> T waitForExpectedCondition(ExpectedCondition<T> condition, int timeoutInSeconds) {
        LogManager.debug("Waiting for ExpectedCondition with timeout: {}s", timeoutInSeconds);
        
        try {
            T result = createWebDriverWait(timeoutInSeconds).until(condition);
            
            LogManager.debug("ExpectedCondition satisfied");
            return result;
            
        } catch (TimeoutException e) {
            LogManager.error("ExpectedCondition not satisfied within {} seconds", timeoutInSeconds);
            throw new TimeoutException("ExpectedCondition not satisfied within " + timeoutInSeconds + " seconds", e);
        }
    }
    
    // ========== UTILITY METHODS ==========
    
    /**
     * Sleeps for a specified duration (use sparingly - prefer explicit waits)
     * 
     * @param milliseconds The duration to sleep in milliseconds
     */
    public static void sleep(long milliseconds) {
        LogManager.warn("Using Thread.sleep for {} milliseconds - consider using explicit waits instead", milliseconds);
        
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogManager.error("Sleep interrupted: {}", e.getMessage());
        }
    }
    
    /**
     * Gets the default explicit wait timeout from configuration
     * 
     * @return Default timeout in seconds
     */
    public static int getDefaultTimeout() {
        return config.getExplicitWaitTimeout();
    }
    
    /**
     * Gets the default polling interval from configuration
     * 
     * @return Default polling interval in seconds
     */
    public static int getDefaultPollingInterval() {
        return FrameworkConstants.POLLING_INTERVAL;
    }
} 