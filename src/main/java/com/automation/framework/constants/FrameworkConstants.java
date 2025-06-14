package com.automation.framework.constants;

/**
 * Framework Constants - Centralizes all constant values used across the framework
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class FrameworkConstants {
    
    // Prevent instantiation
    private FrameworkConstants() {
        throw new UnsupportedOperationException("FrameworkConstants is a utility class and cannot be instantiated");
    }
    
    // ========== TIMEOUT CONSTANTS ==========
    public static final int IMPLICIT_WAIT_TIMEOUT = 10;
    public static final int EXPLICIT_WAIT_TIMEOUT = 20;
    public static final int PAGE_LOAD_TIMEOUT = 30;
    public static final int FLUENT_WAIT_TIMEOUT = 15;
    public static final int POLLING_INTERVAL = 2;
    
    // ========== RETRY CONSTANTS ==========
    public static final int DEFAULT_RETRY_COUNT = 2;
    public static final long RETRY_DELAY_MILLISECONDS = 1000;
    public static final int MAX_RETRY_ATTEMPTS = 3;
    
    // ========== FILE PATH CONSTANTS ==========
    public static final String CONFIG_DIR = "src/test/resources/config/";
    public static final String TESTDATA_DIR = "src/test/resources/testdata/";
    public static final String REPORTS_DIR = "target/allure-results/";
    public static final String SCREENSHOTS_DIR = "target/screenshots/";
    public static final String LOGS_DIR = "logs/";
    
    // ========== BROWSER CONSTANTS ==========
    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    public static final String SAFARI = "safari";
    public static final String EDGE = "edge";
    
    // ========== ENVIRONMENT CONSTANTS ==========
    public static final String DEV_ENVIRONMENT = "dev";
    public static final String QA_ENVIRONMENT = "qa";
    public static final String STAGING_ENVIRONMENT = "staging";
    public static final String PROD_ENVIRONMENT = "prod";
    
    // ========== SYSTEM PROPERTY KEYS ==========
    public static final String BROWSER_PROPERTY = "browser";
    public static final String ENVIRONMENT_PROPERTY = "environment";
    public static final String HEADLESS_PROPERTY = "headless";
    public static final String REMOTE_PROPERTY = "remote";
    public static final String HUB_URL_PROPERTY = "hub.url";
    
    // ========== SCREENSHOT CONSTANTS ==========
    public static final String SCREENSHOT_FORMAT = ".png";
    public static final String SCREENSHOT_PREFIX = "screenshot_";
    public static final String FAILURE_SCREENSHOT_PREFIX = "failure_";
    
    // ========== ALLURE CONSTANTS ==========
    public static final String ALLURE_RESULTS_DIR = "target/allure-results";
    public static final String ALLURE_REPORT_DIR = "target/allure-report";
    
    // ========== LOG CONSTANTS ==========
    public static final String TEST_START_MESSAGE = "Starting test: {}";
    public static final String TEST_END_MESSAGE = "Completed test: {}";
    public static final String TEST_FAILED_MESSAGE = "Test failed: {}";
    public static final String DRIVER_INITIALIZATION_MESSAGE = "Initializing {} driver";
    public static final String DRIVER_QUIT_MESSAGE = "Quitting driver for thread: {}";
    
    // ========== CONFIGURATION FILE NAMES ==========
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String DEV_PROPERTIES = "dev.properties";
    public static final String QA_PROPERTIES = "qa.properties";
    public static final String STAGING_PROPERTIES = "staging.properties";
    public static final String PROD_PROPERTIES = "prod.properties";
    
    // ========== ERROR MESSAGES ==========
    public static final String ELEMENT_NOT_FOUND_ERROR = "Element not found: {}";
    public static final String TIMEOUT_ERROR = "Timeout occurred while waiting for element: {}";
    public static final String DRIVER_INITIALIZATION_ERROR = "Failed to initialize driver: {}";
    public static final String CONFIG_LOAD_ERROR = "Failed to load configuration: {}";
    public static final String SCREENSHOT_ERROR = "Failed to capture screenshot: {}";
    
    // ========== SUCCESS MESSAGES ==========
    public static final String ELEMENT_FOUND_SUCCESS = "Element found successfully: {}";
    public static final String DRIVER_INITIALIZED_SUCCESS = "Driver initialized successfully: {}";
    public static final String CONFIG_LOADED_SUCCESS = "Configuration loaded successfully: {}";
    public static final String SCREENSHOT_CAPTURED_SUCCESS = "Screenshot captured successfully: {}";
} 