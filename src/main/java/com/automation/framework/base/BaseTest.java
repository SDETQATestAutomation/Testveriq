package com.automation.framework.base;

import com.automation.framework.config.ConfigLoader;
import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.driver.DriverFactory;
import com.automation.framework.driver.DriverManager;
import com.automation.framework.exceptions.FrameworkExceptionHandler;
import com.automation.framework.retry.SmartRetryAnalyzer;
import com.automation.framework.utils.LogManager;
import com.automation.framework.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base Test Class - Provides comprehensive test lifecycle management with TestNG integration
 * Handles driver initialization, cleanup, logging, screenshot capture, and error handling
 * Serves as the foundation for all test classes in the framework
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public abstract class BaseTest {
    
    private static final ConfigLoader config = ConfigLoader.getInstance();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    // Test execution tracking
    private LocalDateTime testStartTime;
    private LocalDateTime suiteStartTime;
    
    /**
     * Suite setup - Executed once before all tests in the suite
     * Initializes framework components and prepares test environment
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        suiteStartTime = LocalDateTime.now();
        
        LogManager.info("=================================================================");
        LogManager.info("                    TEST SUITE STARTING                         ");
        LogManager.info("=================================================================");
        LogManager.info("Suite Start Time: {}", suiteStartTime.format(timeFormatter));
        LogManager.info("Environment: {}", config.getProperty("environment", "default"));
        LogManager.info("Browser: {}", config.getBrowser());
        LogManager.info("Headless Mode: {}", config.isHeadless());
        LogManager.info("=================================================================");
        
        // Initialize framework components
        initializeFramework();
        
        // Clean up old screenshots if configured
        int cleanupDays = config.getPropertyAsInt("screenshot.cleanup.days", 7);
        if (cleanupDays > 0) {
            LogManager.info("Cleaning up screenshots older than {} days", cleanupDays);
            int deletedFiles = ScreenshotUtil.cleanupOldScreenshots(cleanupDays);
            LogManager.info("Cleaned up {} old screenshot files", deletedFiles);
        }
    }
    
    /**
     * Suite teardown - Executed once after all tests in the suite
     * Cleans up resources and generates final reports
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        LocalDateTime suiteEndTime = LocalDateTime.now();
        Duration suiteDuration = Duration.between(suiteStartTime, suiteEndTime);
        
        LogManager.info("=================================================================");
        LogManager.info("                    TEST SUITE COMPLETED                        ");
        LogManager.info("=================================================================");
        LogManager.info("Suite End Time: {}", suiteEndTime.format(timeFormatter));
        LogManager.info("Total Duration: {} minutes {} seconds", 
            suiteDuration.toMinutes(), suiteDuration.getSeconds() % 60);
        
        // Print framework statistics
        printFrameworkStatistics();
        
        LogManager.info("=================================================================");
        
        // Clean up framework resources
        cleanupFramework();
    }
    
    /**
     * Test setup - Executed before each test method
     * Initializes WebDriver and prepares test environment
     */
    @BeforeMethod(alwaysRun = true)
    public void testSetup(Method method) {
        testStartTime = LocalDateTime.now();
        String testName = getTestName(method);
        
        LogManager.info("┌─────────────────────────────────────────────────────────────────");
        LogManager.info("│ STARTING TEST: {}", testName);
        LogManager.info("│ Start Time: {}", testStartTime.format(timeFormatter));
        LogManager.info("│ Thread: {}", Thread.currentThread().getName());
        LogManager.info("└─────────────────────────────────────────────────────────────────");
        
        try {
            // Initialize WebDriver for this test
            initializeDriver();
            
            // Configure driver timeouts
            configureDriver();
            
            // Perform any custom test setup
            customTestSetup(method);
            
            LogManager.info("Test setup completed successfully for: {}", testName);
            
        } catch (Exception e) {
            LogManager.error("Test setup failed for {}: {}", testName, e.getMessage());
            FrameworkExceptionHandler.handleException(e, "Test Setup", testName);
            throw e;
        }
    }
    
    /**
     * Test teardown - Executed after each test method
     * Handles cleanup, screenshot capture, and logging
     */
    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        LocalDateTime testEndTime = LocalDateTime.now();
        Duration testDuration = Duration.between(testStartTime, testEndTime);
        String testName = getTestName(result);
        
        try {
            // Handle test result
            handleTestResult(result, testName, testDuration);
            
            // Perform custom test cleanup
            customTestTeardown(result);
            
        } catch (Exception e) {
            LogManager.warn("Error during test teardown for {}: {}", testName, e.getMessage());
        } finally {
            // Always clean up driver
            cleanupDriver();
            
            LogManager.info("┌─────────────────────────────────────────────────────────────────");
            LogManager.info("│ COMPLETED TEST: {}", testName);
            LogManager.info("│ End Time: {}", testEndTime.format(timeFormatter));
            LogManager.info("│ Duration: {}.{} seconds", testDuration.getSeconds(), testDuration.getNano() / 1_000_000);
            LogManager.info("│ Status: {}", getResultStatus(result));
            LogManager.info("└─────────────────────────────────────────────────────────────────");
        }
    }
    
    /**
     * Initializes the WebDriver for the current test
     */
    private void initializeDriver() {
        String browserType = config.getBrowser();
        boolean headless = config.isHeadless();
        
        LogManager.debug("Initializing {} driver (headless: {})", browserType, headless);
        
        WebDriver driver;
        if (headless) {
            driver = DriverFactory.createHeadlessDriver(browserType);
        } else {
            driver = DriverFactory.createDriver(browserType);
        }
        
        DriverManager.setDriver(driver);
        DriverManager.setBrowserType(browserType);
        
        LogManager.debug("Driver initialized successfully: {}", driver.getClass().getSimpleName());
    }
    
    /**
     * Configures the WebDriver with timeouts and other settings
     */
    private void configureDriver() {
        WebDriver driver = DriverManager.getDriver();
        
        // Configure timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FrameworkConstants.IMPLICIT_WAIT_TIMEOUT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(FrameworkConstants.PAGE_LOAD_TIMEOUT));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(FrameworkConstants.EXPLICIT_WAIT_TIMEOUT));
        
        // Maximize window if not headless
        if (!config.isHeadless()) {
            driver.manage().window().maximize();
            LogManager.debug("Browser window maximized");
        }
        
        LogManager.debug("Driver configured with timeouts - Implicit: {}s, Page Load: {}s, Script: {}s",
            FrameworkConstants.IMPLICIT_WAIT_TIMEOUT, FrameworkConstants.PAGE_LOAD_TIMEOUT, FrameworkConstants.EXPLICIT_WAIT_TIMEOUT);
    }
    
    /**
     * Handles test result based on success or failure
     * 
     * @param result Test result
     * @param testName Test name
     * @param duration Test duration
     */
    private void handleTestResult(ITestResult result, String testName, Duration duration) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                handleTestSuccess(result, testName, duration);
                break;
                
            case ITestResult.FAILURE:
                handleTestFailure(result, testName, duration);
                break;
                
            case ITestResult.SKIP:
                handleTestSkipped(result, testName, duration);
                break;
                
            default:
                LogManager.warn("Unknown test result status: {} for test: {}", result.getStatus(), testName);
        }
    }
    
    /**
     * Handles successful test completion
     * 
     * @param result Test result
     * @param testName Test name
     * @param duration Test duration
     */
    private void handleTestSuccess(ITestResult result, String testName, Duration duration) {
        LogManager.info("✅ TEST PASSED: {} (Duration: {}.{}s)", 
            testName, duration.getSeconds(), duration.getNano() / 1_000_000);
        
        // Capture success screenshot if configured
        if (config.getPropertyAsBoolean("screenshot.on.success", false)) {
            ScreenshotUtil.captureScreenshot("success_" + testName);
        }
    }
    
    /**
     * Handles test failure
     * 
     * @param result Test result
     * @param testName Test name
     * @param duration Test duration
     */
    private void handleTestFailure(ITestResult result, String testName, Duration duration) {
        Throwable exception = result.getThrowable();
        
        LogManager.error("❌ TEST FAILED: {} (Duration: {}.{}s)", 
            testName, duration.getSeconds(), duration.getNano() / 1_000_000);
        
        if (exception != null) {
            LogManager.error("Failure Reason: {}", exception.getMessage());
        }
        
        // Capture failure screenshot
        String screenshotPath = ScreenshotUtil.captureFailureScreenshot(testName);
        if (screenshotPath != null) {
            LogManager.error("Failure Screenshot: {}", screenshotPath);
        }
        
        // Handle exception through framework handler
        if (exception != null) {
            FrameworkExceptionHandler.handleException(exception, "Test Execution", testName);
        }
    }
    
    /**
     * Handles skipped test
     * 
     * @param result Test result
     * @param testName Test name
     * @param duration Test duration
     */
    private void handleTestSkipped(ITestResult result, String testName, Duration duration) {
        LogManager.warn("⏭️  TEST SKIPPED: {} (Duration: {}.{}s)", 
            testName, duration.getSeconds(), duration.getNano() / 1_000_000);
        
        if (result.getThrowable() != null) {
            LogManager.warn("Skip Reason: {}", result.getThrowable().getMessage());
        }
    }
    
    /**
     * Cleans up the WebDriver after test completion
     */
    private void cleanupDriver() {
        try {
            if (DriverManager.hasDriver()) {
                LogManager.debug("Cleaning up WebDriver for thread: {}", Thread.currentThread().getName());
                DriverManager.quitDriver();
            }
        } catch (Exception e) {
            LogManager.warn("Error during driver cleanup: {}", e.getMessage());
        }
    }
    
    /**
     * Initializes framework components
     */
    private void initializeFramework() {
        LogManager.info("Initializing Test Automation Framework...");
        
        // Clear any existing retry data
        SmartRetryAnalyzer.clearRetryData();
        
        // Clear exception data
        FrameworkExceptionHandler.clearExceptionData();
        
        LogManager.info("Framework initialization completed");
    }
    
    /**
     * Cleans up framework resources
     */
    private void cleanupFramework() {
        LogManager.info("Cleaning up framework resources...");
        
        // Force cleanup of any remaining drivers
        try {
            DriverManager.quitDriver();
        } catch (Exception e) {
            LogManager.debug("Driver cleanup during framework shutdown: {}", e.getMessage());
        }
        
        LogManager.info("Framework cleanup completed");
    }
    
    /**
     * Prints framework statistics
     */
    private void printFrameworkStatistics() {
        LogManager.info("Framework Statistics:");
        
        // Retry statistics
        String retryStats = SmartRetryAnalyzer.getRetryStatistics();
        LogManager.info("Retry Statistics: {}", retryStats);
        
        // Exception statistics
        String exceptionStats = FrameworkExceptionHandler.getExceptionSummary();
        LogManager.info("Exception Summary:");
        LogManager.info(exceptionStats);
        
        // Screenshot directory info
        String screenshotDir = ScreenshotUtil.getScreenshotsDirectory();
        LogManager.info("Screenshots Directory: {}", screenshotDir);
    }
    
    /**
     * Gets test name from method
     * 
     * @param method Test method
     * @return Test name
     */
    private String getTestName(Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }
    
    /**
     * Gets test name from test result
     * 
     * @param result Test result
     * @return Test name
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
    
    /**
     * Gets result status as string
     * 
     * @param result Test result
     * @return Status string
     */
    private String getResultStatus(ITestResult result) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "UNKNOWN";
        };
    }
    
    // Abstract/overridable methods for custom behavior
    
    /**
     * Custom test setup that can be overridden by subclasses
     * 
     * @param method Test method being executed
     */
    protected void customTestSetup(Method method) {
        // Default implementation - can be overridden
        LogManager.debug("Custom test setup executed for: {}", method.getName());
    }
    
    /**
     * Custom test teardown that can be overridden by subclasses
     * 
     * @param result Test result
     */
    protected void customTestTeardown(ITestResult result) {
        // Default implementation - can be overridden
        LogManager.debug("Custom test teardown executed for: {}", result.getMethod().getMethodName());
    }
    
    // Utility methods for test classes
    
    /**
     * Gets the current WebDriver instance
     * 
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }
    
    /**
     * Navigates to a URL
     * 
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        LogManager.info("Navigating to: {}", url);
        getDriver().get(url);
        LogManager.info("Navigation completed to: {}", url);
    }
    
    /**
     * Gets the base URL from configuration
     * 
     * @return Base URL
     */
    protected String getBaseUrl() {
        return config.getBaseUrl();
    }
    
    /**
     * Captures a screenshot with custom name
     * 
     * @param screenshotName Name for the screenshot
     * @return Path to captured screenshot
     */
    protected String captureScreenshot(String screenshotName) {
        return ScreenshotUtil.captureScreenshot(screenshotName);
    }
    
    /**
     * Waits for specified duration
     * 
     * @param milliseconds Duration to wait in milliseconds
     */
    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogManager.warn("Sleep interrupted: {}", e.getMessage());
        }
    }
} 