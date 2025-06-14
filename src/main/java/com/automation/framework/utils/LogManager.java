package com.automation.framework.utils;

import org.apache.logging.log4j.Logger;
import com.automation.framework.constants.FrameworkConstants;

/**
 * Log Manager - Provides centralized logging functionality using Log4J2
 * Supports standardized logging formats for different types of messages
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class LogManager {
    
    private static final Logger FRAMEWORK_LOGGER = org.apache.logging.log4j.LogManager.getLogger("com.automation.framework");
    private static final Logger TEST_LOGGER = org.apache.logging.log4j.LogManager.getLogger("com.automation.tests");
    
    // Prevent instantiation
    private LogManager() {
        throw new UnsupportedOperationException("LogManager is a utility class and cannot be instantiated");
    }
    
    /**
     * Logs informational messages for framework operations
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void info(String message, Object... args) {
        FRAMEWORK_LOGGER.info(message, args);
    }
    
    /**
     * Logs debug messages for detailed troubleshooting
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void debug(String message, Object... args) {
        FRAMEWORK_LOGGER.debug(message, args);
    }
    
    /**
     * Logs warning messages for potential issues
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void warn(String message, Object... args) {
        FRAMEWORK_LOGGER.warn(message, args);
    }
    
    /**
     * Logs error messages for exceptions and failures
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void error(String message, Object... args) {
        FRAMEWORK_LOGGER.error(message, args);
    }
    
    /**
     * Logs error messages with exception details
     * 
     * @param message The message to log
     * @param throwable The exception that occurred
     */
    public static void error(String message, Throwable throwable) {
        FRAMEWORK_LOGGER.error(message, throwable);
    }
    
    /**
     * Logs test-specific informational messages
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void testInfo(String message, Object... args) {
        TEST_LOGGER.info(message, args);
    }
    
    /**
     * Logs test-specific debug messages
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void testDebug(String message, Object... args) {
        TEST_LOGGER.debug(message, args);
    }
    
    /**
     * Logs test-specific warning messages
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void testWarn(String message, Object... args) {
        TEST_LOGGER.warn(message, args);
    }
    
    /**
     * Logs test-specific error messages
     * 
     * @param message The message to log
     * @param args Optional arguments for message formatting
     */
    public static void testError(String message, Object... args) {
        TEST_LOGGER.error(message, args);
    }
    
    /**
     * Logs test-specific error messages with exception details
     * 
     * @param message The message to log
     * @param throwable The exception that occurred
     */
    public static void testError(String message, Throwable throwable) {
        TEST_LOGGER.error(message, throwable);
    }
    
    // ========== STANDARDIZED LOGGING METHODS ==========
    
    /**
     * Logs the start of a test method
     * 
     * @param testName The name of the test being started
     */
    public static void logTestStart(String testName) {
        testInfo(FrameworkConstants.TEST_START_MESSAGE, testName);
    }
    
    /**
     * Logs the completion of a test method
     * 
     * @param testName The name of the test that completed
     */
    public static void logTestEnd(String testName) {
        testInfo(FrameworkConstants.TEST_END_MESSAGE, testName);
    }
    
    /**
     * Logs test failure with exception details
     * 
     * @param testName The name of the test that failed
     * @param throwable The exception that caused the failure
     */
    public static void logTestFailure(String testName, Throwable throwable) {
        testError(FrameworkConstants.TEST_FAILED_MESSAGE, testName);
        testError("Exception details: ", throwable);
    }
    
    /**
     * Logs driver initialization
     * 
     * @param browserType The type of browser being initialized
     */
    public static void logDriverInitialization(String browserType) {
        info(FrameworkConstants.DRIVER_INITIALIZATION_MESSAGE, browserType);
    }
    
    /**
     * Logs driver quit operation
     * 
     * @param threadId The thread ID for which the driver is being quit
     */
    public static void logDriverQuit(String threadId) {
        info(FrameworkConstants.DRIVER_QUIT_MESSAGE, threadId);
    }
    
    /**
     * Logs element interaction success
     * 
     * @param elementDescription Description of the element
     */
    public static void logElementFound(String elementDescription) {
        debug(FrameworkConstants.ELEMENT_FOUND_SUCCESS, elementDescription);
    }
    
    /**
     * Logs element interaction failure
     * 
     * @param elementDescription Description of the element
     */
    public static void logElementNotFound(String elementDescription) {
        error(FrameworkConstants.ELEMENT_NOT_FOUND_ERROR, elementDescription);
    }
    
    /**
     * Logs timeout errors
     * 
     * @param elementDescription Description of the element that timed out
     */
    public static void logTimeout(String elementDescription) {
        error(FrameworkConstants.TIMEOUT_ERROR, elementDescription);
    }
    
    /**
     * Logs configuration loading success
     * 
     * @param configFile The configuration file that was loaded
     */
    public static void logConfigLoaded(String configFile) {
        info(FrameworkConstants.CONFIG_LOADED_SUCCESS, configFile);
    }
    
    /**
     * Logs configuration loading failure
     * 
     * @param configFile The configuration file that failed to load
     * @param throwable The exception that occurred
     */
    public static void logConfigLoadFailure(String configFile, Throwable throwable) {
        error(FrameworkConstants.CONFIG_LOAD_ERROR, configFile);
        error("Configuration load exception: ", throwable);
    }
    
    /**
     * Logs screenshot capture success
     * 
     * @param screenshotPath The path where the screenshot was saved
     */
    public static void logScreenshotCaptured(String screenshotPath) {
        info(FrameworkConstants.SCREENSHOT_CAPTURED_SUCCESS, screenshotPath);
    }
    
    /**
     * Logs screenshot capture failure
     * 
     * @param throwable The exception that occurred during screenshot capture
     */
    public static void logScreenshotFailure(Throwable throwable) {
        error(FrameworkConstants.SCREENSHOT_ERROR, throwable.getMessage());
        error("Screenshot capture exception: ", throwable);
    }
    
    /**
     * Logs method entry for debugging purposes
     * 
     * @param className The name of the class
     * @param methodName The name of the method
     */
    public static void logMethodEntry(String className, String methodName) {
        debug("Entering method: {}.{}", className, methodName);
    }
    
    /**
     * Logs method exit for debugging purposes
     * 
     * @param className The name of the class
     * @param methodName The name of the method
     */
    public static void logMethodExit(String className, String methodName) {
        debug("Exiting method: {}.{}", className, methodName);
    }
} 