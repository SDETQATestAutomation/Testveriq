package com.automation.framework.utils;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screenshot Utility - Provides automated screenshot capture functionality
 * Integrates with Allure reporting for evidence attachment and failure documentation
 * Supports various screenshot capture scenarios with automatic file management
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class ScreenshotUtil {
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
    
    // Prevent instantiation
    private ScreenshotUtil() {
        throw new UnsupportedOperationException("ScreenshotUtil is a utility class and cannot be instantiated");
    }
    
    /**
     * Captures a screenshot and returns the file path
     * 
     * @return Path to the captured screenshot file
     */
    public static String captureScreenshot() {
        return captureScreenshot("screenshot");
    }
    
    /**
     * Captures a screenshot with a custom name
     * 
     * @param screenshotName Custom name for the screenshot
     * @return Path to the captured screenshot file
     */
    public static String captureScreenshot(String screenshotName) {
        try {
            if (!DriverManager.hasDriver()) {
                LogManager.warn("No WebDriver available for screenshot capture");
                return null;
            }
            
            WebDriver driver = DriverManager.getDriver();
            
            if (!(driver instanceof TakesScreenshot)) {
                LogManager.warn("Driver does not support screenshot capture: {}", driver.getClass().getSimpleName());
                return null;
            }
            
            // Capture screenshot as byte array
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            
            // Generate unique filename
            String timestamp = dateFormat.format(new Date());
            String filename = String.format("%s_%s_%s%s", 
                FrameworkConstants.SCREENSHOT_PREFIX, screenshotName, timestamp, FrameworkConstants.SCREENSHOT_FORMAT);
            
            // Create screenshots directory if it doesn't exist
            Path screenshotsDir = Paths.get(FrameworkConstants.SCREENSHOTS_DIR);
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }
            
            // Save screenshot to file
            Path screenshotPath = screenshotsDir.resolve(filename);
            Files.write(screenshotPath, screenshotBytes);
            
            String absolutePath = screenshotPath.toAbsolutePath().toString();
            LogManager.logScreenshotCaptured(absolutePath);
            
            return absolutePath;
            
        } catch (Exception e) {
            LogManager.logScreenshotFailure(e);
            return null;
        }
    }
    
    /**
     * Captures a screenshot on test failure
     * 
     * @param testName Name of the failed test
     * @return Path to the captured screenshot file
     */
    public static String captureFailureScreenshot(String testName) {
        String screenshotName = FrameworkConstants.FAILURE_SCREENSHOT_PREFIX + testName.replaceAll("[^a-zA-Z0-9]", "_");
        return captureScreenshot(screenshotName);
    }
    
    /**
     * Captures a screenshot for retry attempts
     * 
     * @param testName Name of the test being retried
     * @param retryAttempt Retry attempt number
     * @return Path to the captured screenshot file
     */
    public static String captureRetryScreenshot(String testName, int retryAttempt) {
        String screenshotName = String.format("retry_%s_attempt_%d", 
            testName.replaceAll("[^a-zA-Z0-9]", "_"), retryAttempt);
        return captureScreenshot(screenshotName);
    }
    
    /**
     * Captures screenshot and attaches to Allure report
     * 
     * @param screenshotName Name for the screenshot
     * @return Screenshot bytes for Allure attachment
     */
    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] captureScreenshotForAllure(String screenshotName) {
        try {
            if (!DriverManager.hasDriver()) {
                LogManager.warn("No WebDriver available for Allure screenshot capture");
                return new byte[0];
            }
            
            WebDriver driver = DriverManager.getDriver();
            
            if (!(driver instanceof TakesScreenshot)) {
                LogManager.warn("Driver does not support screenshot capture for Allure");
                return new byte[0];
            }
            
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            
            // Also save to file for reference
            captureScreenshot(screenshotName);
            
            LogManager.debug("Screenshot captured and attached to Allure report: {}", screenshotName);
            return screenshotBytes;
            
        } catch (Exception e) {
            LogManager.logScreenshotFailure(e);
            return new byte[0];
        }
    }
    
    /**
     * Captures screenshot and attaches to Allure with default name
     * 
     * @return Screenshot bytes for Allure attachment
     */
    @Attachment(value = "Page Screenshot", type = "image/png")
    public static byte[] captureScreenshotForAllure() {
        return captureScreenshotForAllure("allure_screenshot");
    }
    
    /**
     * Captures failure screenshot and attaches to Allure
     * 
     * @param testName Name of the failed test
     * @return Screenshot bytes for Allure attachment
     */
    @Attachment(value = "Failure Screenshot", type = "image/png")
    public static byte[] captureFailureScreenshotForAllure(String testName) {
        return captureScreenshotForAllure(FrameworkConstants.FAILURE_SCREENSHOT_PREFIX + testName);
    }
    
    /**
     * Attaches an existing screenshot file to Allure
     * 
     * @param screenshotPath Path to the screenshot file
     * @param attachmentName Name for the Allure attachment
     */
    public static void attachScreenshotToAllure(String screenshotPath, String attachmentName) {
        try {
            if (screenshotPath == null || screenshotPath.isEmpty()) {
                LogManager.warn("Cannot attach null or empty screenshot path to Allure");
                return;
            }
            
            Path path = Paths.get(screenshotPath);
            if (!Files.exists(path)) {
                LogManager.warn("Screenshot file does not exist: {}", screenshotPath);
                return;
            }
            
            byte[] screenshotBytes = Files.readAllBytes(path);
            Allure.addAttachment(attachmentName, "image/png", new ByteArrayInputStream(screenshotBytes), FrameworkConstants.SCREENSHOT_FORMAT);
            
            LogManager.debug("Screenshot attached to Allure: {} -> {}", screenshotPath, attachmentName);
            
        } catch (IOException e) {
            LogManager.error("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }
    
    /**
     * Captures screenshot with current page information
     * 
     * @return Screenshot information object
     */
    public static ScreenshotInfo captureScreenshotWithInfo() {
        try {
            if (!DriverManager.hasDriver()) {
                return new ScreenshotInfo(null, "No driver available", "", "");
            }
            
            WebDriver driver = DriverManager.getDriver();
            String screenshotPath = captureScreenshot("info_capture");
            
            String currentUrl = "";
            String pageTitle = "";
            
            try {
                currentUrl = driver.getCurrentUrl();
                pageTitle = driver.getTitle();
            } catch (Exception e) {
                LogManager.debug("Could not get page information: {}", e.getMessage());
            }
            
            return new ScreenshotInfo(screenshotPath, "Screenshot captured successfully", currentUrl, pageTitle);
            
        } catch (Exception e) {
            LogManager.logScreenshotFailure(e);
            return new ScreenshotInfo(null, "Screenshot capture failed: " + e.getMessage(), "", "");
        }
    }
    
    /**
     * Cleans up old screenshot files
     * 
     * @param daysOld Number of days old to consider for cleanup
     * @return Number of files deleted
     */
    public static int cleanupOldScreenshots(int daysOld) {
        int filesDeleted = 0;
        
        try {
            Path screenshotsDir = Paths.get(FrameworkConstants.SCREENSHOTS_DIR);
            
            if (!Files.exists(screenshotsDir)) {
                LogManager.debug("Screenshots directory does not exist: {}", screenshotsDir);
                return 0;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
            
            Files.walk(screenshotsDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(FrameworkConstants.SCREENSHOT_FORMAT))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            LogManager.debug("Deleted old screenshot: {}", path);
                        } catch (IOException e) {
                            LogManager.warn("Failed to delete old screenshot: {}", path);
                        }
                    });
            
            LogManager.info("Cleaned up {} old screenshot files (older than {} days)", filesDeleted, daysOld);
            
        } catch (Exception e) {
            LogManager.error("Error during screenshot cleanup: {}", e.getMessage());
        }
        
        return filesDeleted;
    }
    
    /**
     * Gets the screenshots directory path
     * 
     * @return Path to screenshots directory
     */
    public static String getScreenshotsDirectory() {
        return FrameworkConstants.SCREENSHOTS_DIR;
    }
    
    /**
     * Checks if the current driver supports screenshot capture
     * 
     * @return true if driver supports screenshots
     */
    public static boolean isScreenshotCapable() {
        try {
            return DriverManager.hasDriver() && (DriverManager.getDriver() instanceof TakesScreenshot);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Inner class to hold screenshot information
     */
    public static class ScreenshotInfo {
        private final String filePath;
        private final String status;
        private final String currentUrl;
        private final String pageTitle;
        private final long timestamp;
        
        public ScreenshotInfo(String filePath, String status, String currentUrl, String pageTitle) {
            this.filePath = filePath;
            this.status = status;
            this.currentUrl = currentUrl;
            this.pageTitle = pageTitle;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getFilePath() { return filePath; }
        public String getStatus() { return status; }
        public String getCurrentUrl() { return currentUrl; }
        public String getPageTitle() { return pageTitle; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("ScreenshotInfo[path=%s, status=%s, url=%s, title=%s, timestamp=%d]",
                filePath, status, currentUrl, pageTitle, timestamp);
        }
    }
} 