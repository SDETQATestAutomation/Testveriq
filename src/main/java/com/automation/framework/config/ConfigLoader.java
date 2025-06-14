package com.automation.framework.config;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.utils.LogManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Config Loader - Singleton class for loading and managing configuration properties
 * Supports environment-specific configuration files with fallback to default values
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class ConfigLoader {
    
    private static ConfigLoader instance;
    private static final Object lock = new Object();
    private Properties properties;
    private String currentEnvironment;
    
    // Private constructor to prevent instantiation
    private ConfigLoader() {
        loadConfiguration();
    }
    
    /**
     * Gets the singleton instance of ConfigLoader
     * Thread-safe implementation using double-checked locking
     * 
     * @return ConfigLoader instance
     */
    public static ConfigLoader getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConfigLoader();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads configuration from environment-specific properties file
     * Falls back to application.properties if environment-specific file not found
     */
    private void loadConfiguration() {
        properties = new Properties();
        currentEnvironment = getEnvironment();
        
        try {
            // Load base application properties first
            loadPropertiesFile(FrameworkConstants.APPLICATION_PROPERTIES);
            
            // Override with environment-specific properties
            String envPropertiesFile = getEnvironmentPropertiesFile(currentEnvironment);
            loadPropertiesFile(envPropertiesFile);
            
            LogManager.logConfigLoaded(envPropertiesFile);
            
        } catch (Exception e) {
            LogManager.logConfigLoadFailure("configuration files", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    /**
     * Loads properties from a specific file
     * 
     * @param fileName The name of the properties file to load
     */
    private void loadPropertiesFile(String fileName) {
        String filePath = FrameworkConstants.CONFIG_DIR + fileName;
        
        try (InputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
            LogManager.debug("Loaded properties from: {}", filePath);
            
        } catch (IOException e) {
            LogManager.warn("Could not load properties file: {}. Continuing with existing properties.", filePath);
            
            // Try to load from classpath as fallback
            try (InputStream classpathStream = getClass().getClassLoader().getResourceAsStream("config/" + fileName)) {
                if (classpathStream != null) {
                    properties.load(classpathStream);
                    LogManager.debug("Loaded properties from classpath: {}", fileName);
                } else {
                    LogManager.warn("Properties file not found in classpath: {}", fileName);
                }
            } catch (IOException classpathException) {
                LogManager.warn("Failed to load properties from classpath: {}", fileName);
            }
        }
    }
    
    /**
     * Gets the current environment from system properties or defaults to QA
     * 
     * @return The current environment
     */
    private String getEnvironment() {
        String env = System.getProperty(FrameworkConstants.ENVIRONMENT_PROPERTY);
        if (env == null || env.trim().isEmpty()) {
            env = FrameworkConstants.QA_ENVIRONMENT; // Default to QA
        }
        LogManager.info("Current environment: {}", env);
        return env.toLowerCase();
    }
    
    /**
     * Gets the properties file name based on environment
     * 
     * @param environment The environment name
     * @return The properties file name for the environment
     */
    private String getEnvironmentPropertiesFile(String environment) {
        return switch (environment.toLowerCase()) {
            case FrameworkConstants.DEV_ENVIRONMENT -> FrameworkConstants.DEV_PROPERTIES;
            case FrameworkConstants.QA_ENVIRONMENT -> FrameworkConstants.QA_PROPERTIES;
            case FrameworkConstants.STAGING_ENVIRONMENT -> FrameworkConstants.STAGING_PROPERTIES;
            case FrameworkConstants.PROD_ENVIRONMENT -> FrameworkConstants.PROD_PROPERTIES;
            default -> FrameworkConstants.QA_PROPERTIES; // Default to QA
        };
    }
    
    /**
     * Gets a property value by key
     * 
     * @param key The property key
     * @return The property value or null if not found
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            LogManager.warn("Property not found: {}", key);
        }
        return value;
    }
    
    /**
     * Gets a property value by key with a default value
     * 
     * @param key The property key
     * @param defaultValue The default value to return if key is not found
     * @return The property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        if (value.equals(defaultValue)) {
            LogManager.debug("Using default value for property {}: {}", key, defaultValue);
        }
        return value;
    }
    
    /**
     * Gets a property value as integer
     * 
     * @param key The property key
     * @param defaultValue The default value to return if key is not found or invalid
     * @return The property value as integer
     */
    public int getPropertyAsInt(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LogManager.warn("Invalid integer value for property {}: {}. Using default: {}", key, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    /**
     * Gets a property value as boolean
     * 
     * @param key The property key
     * @param defaultValue The default value to return if key is not found
     * @return The property value as boolean
     */
    public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    /**
     * Gets the current environment
     * 
     * @return The current environment
     */
    public String getCurrentEnvironment() {
        return currentEnvironment;
    }
    
    // ========== COMMONLY USED CONFIGURATION GETTERS ==========
    
    /**
     * Gets the base URL for the application under test
     * 
     * @return The base URL
     */
    public String getBaseUrl() {
        return getProperty("app.base.url", "http://localhost:8080");
    }
    
    /**
     * Gets the browser type from configuration
     * 
     * @return The browser type
     */
    public String getBrowser() {
        return getProperty(FrameworkConstants.BROWSER_PROPERTY, FrameworkConstants.CHROME);
    }
    
    /**
     * Gets whether to run in headless mode
     * 
     * @return true if headless mode is enabled
     */
    public boolean isHeadless() {
        return getPropertyAsBoolean(FrameworkConstants.HEADLESS_PROPERTY, false);
    }
    
    /**
     * Gets whether to run tests remotely
     * 
     * @return true if remote execution is enabled
     */
    public boolean isRemote() {
        return getPropertyAsBoolean(FrameworkConstants.REMOTE_PROPERTY, false);
    }
    
    /**
     * Gets the Selenium Grid hub URL
     * 
     * @return The hub URL
     */
    public String getHubUrl() {
        return getProperty(FrameworkConstants.HUB_URL_PROPERTY, "http://localhost:4444/wd/hub");
    }
    
    /**
     * Gets the implicit wait timeout
     * 
     * @return The implicit wait timeout in seconds
     */
    public int getImplicitWaitTimeout() {
        return getPropertyAsInt("implicit.wait.timeout", FrameworkConstants.IMPLICIT_WAIT_TIMEOUT);
    }
    
    /**
     * Gets the explicit wait timeout
     * 
     * @return The explicit wait timeout in seconds
     */
    public int getExplicitWaitTimeout() {
        return getPropertyAsInt("explicit.wait.timeout", FrameworkConstants.EXPLICIT_WAIT_TIMEOUT);
    }
    
    /**
     * Gets the page load timeout
     * 
     * @return The page load timeout in seconds
     */
    public int getPageLoadTimeout() {
        return getPropertyAsInt("page.load.timeout", FrameworkConstants.PAGE_LOAD_TIMEOUT);
    }
    
    /**
     * Reloads the configuration (useful for dynamic configuration updates)
     */
    public void reloadConfiguration() {
        LogManager.info("Reloading configuration for environment: {}", currentEnvironment);
        loadConfiguration();
    }
} 