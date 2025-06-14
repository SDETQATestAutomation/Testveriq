package com.automation.framework.driver;

import com.automation.framework.config.ConfigLoader;
import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.utils.LogManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Driver Factory - Creates and configures WebDriver instances
 * Supports local and remote execution with multiple browsers
 * Integrates with WebDriverManager for automatic driver management
 * 
 * @author Test Automation Framework
 * @version 1.0.0
 */
public final class DriverFactory {
    
    private static final ConfigLoader config = ConfigLoader.getInstance();
    
    // Prevent instantiation
    private DriverFactory() {
        throw new UnsupportedOperationException("DriverFactory is a utility class and cannot be instantiated");
    }
    
    /**
     * Creates a WebDriver instance based on configuration
     * 
     * @return WebDriver instance
     */
    public static WebDriver createDriver() {
        String browserType = config.getBrowser().toLowerCase();
        boolean isRemote = config.isRemote();
        
        LogManager.logDriverInitialization(browserType);
        
        WebDriver driver;
        
        if (isRemote) {
            driver = createRemoteDriver(browserType);
        } else {
            driver = createLocalDriver(browserType);
        }
        
        configureDriver(driver);
        
        LogManager.info(FrameworkConstants.DRIVER_INITIALIZED_SUCCESS, browserType);
        return driver;
    }
    
    /**
     * Creates a local WebDriver instance
     * 
     * @param browserType The type of browser to create
     * @return WebDriver instance
     */
    private static WebDriver createLocalDriver(String browserType) {
        return switch (browserType) {
            case FrameworkConstants.CHROME -> createChromeDriver();
            case FrameworkConstants.FIREFOX -> createFirefoxDriver();
            case FrameworkConstants.SAFARI -> createSafariDriver();
            case FrameworkConstants.EDGE -> createEdgeDriver();
            default -> {
                LogManager.warn("Unsupported browser type: {}. Defaulting to Chrome.", browserType);
                yield createChromeDriver();
            }
        };
    }
    
    /**
     * Creates a remote WebDriver instance for Selenium Grid
     * 
     * @param browserType The type of browser to create
     * @return RemoteWebDriver instance
     */
    private static WebDriver createRemoteDriver(String browserType) {
        try {
            URL hubUrl = new URL(config.getHubUrl());
            
            return switch (browserType) {
                case FrameworkConstants.CHROME -> new RemoteWebDriver(hubUrl, getChromeOptions());
                case FrameworkConstants.FIREFOX -> new RemoteWebDriver(hubUrl, getFirefoxOptions());
                case FrameworkConstants.SAFARI -> new RemoteWebDriver(hubUrl, getSafariOptions());
                case FrameworkConstants.EDGE -> new RemoteWebDriver(hubUrl, getEdgeOptions());
                default -> {
                    LogManager.warn("Unsupported browser type for remote execution: {}. Defaulting to Chrome.", browserType);
                    yield new RemoteWebDriver(hubUrl, getChromeOptions());
                }
            };
            
        } catch (MalformedURLException e) {
            LogManager.error(FrameworkConstants.DRIVER_INITIALIZATION_ERROR, "Invalid hub URL: " + config.getHubUrl());
            throw new RuntimeException("Failed to create remote driver", e);
        }
    }
    
    /**
     * Creates a Chrome WebDriver instance
     * 
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(getChromeOptions());
    }
    
    /**
     * Creates a Firefox WebDriver instance
     * 
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(getFirefoxOptions());
    }
    
    /**
     * Creates a Safari WebDriver instance
     * 
     * @return SafariDriver instance
     */
    private static WebDriver createSafariDriver() {
        // Safari driver doesn't need WebDriverManager setup
        return new SafariDriver(getSafariOptions());
    }
    
    /**
     * Creates an Edge WebDriver instance
     * 
     * @return EdgeDriver instance
     */
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver(getEdgeOptions());
    }
    
    /**
     * Gets Chrome browser options
     * 
     * @return ChromeOptions instance
     */
    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        // Common Chrome arguments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        
        // Headless mode
        if (config.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Additional security and stability options
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        
        return options;
    }
    
    /**
     * Gets Firefox browser options
     * 
     * @return FirefoxOptions instance
     */
    private static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        
        // Headless mode
        if (config.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        
        // Additional Firefox preferences
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.volume_scale", "0.0");
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", 
            "application/pdf,application/octet-stream,application/x-winzip,application/x-pdf,application/pdf");
        
        return options;
    }
    
    /**
     * Gets Safari browser options
     * 
     * @return SafariOptions instance
     */
    private static SafariOptions getSafariOptions() {
        SafariOptions options = new SafariOptions();
        
        // Safari specific options
        options.setAutomaticInspection(false);
        options.setAutomaticProfiling(false);
        
        return options;
    }
    
    /**
     * Gets Edge browser options
     * 
     * @return EdgeOptions instance
     */
    private static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        
        // Common Edge arguments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        
        // Headless mode
        if (config.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--window-size=1920,1080");
        }
        
        return options;
    }
    
    /**
     * Configures the WebDriver with timeouts and other settings
     * 
     * @param driver The WebDriver instance to configure
     */
    private static void configureDriver(WebDriver driver) {
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWaitTimeout()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        
        // Maximize window if not headless
        if (!config.isHeadless()) {
            try {
                driver.manage().window().maximize();
            } catch (Exception e) {
                LogManager.warn("Could not maximize browser window: {}", e.getMessage());
            }
        }
        
        LogManager.debug("Driver configured with timeouts - Implicit: {}s, Page Load: {}s", 
            config.getImplicitWaitTimeout(), config.getPageLoadTimeout());
    }
    
    /**
     * Creates a WebDriver instance with specific browser type (overrides configuration)
     * 
     * @param browserType The browser type to create
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browserType) {
        LogManager.info("Creating driver with override browser type: {}", browserType);
        
        WebDriver driver = createLocalDriver(browserType.toLowerCase());
        configureDriver(driver);
        
        return driver;
    }
    
    /**
     * Creates a headless WebDriver instance
     * 
     * @param browserType The browser type to create
     * @return WebDriver instance
     */
    public static WebDriver createHeadlessDriver(String browserType) {
        LogManager.info("Creating headless driver for browser: {}", browserType);
        
        // Temporarily override headless setting
        boolean originalHeadless = config.isHeadless();
        
        WebDriver driver;
        try {
            // Create driver with headless options
            driver = switch (browserType.toLowerCase()) {
                case FrameworkConstants.CHROME -> {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = getChromeOptions();
                    options.addArguments("--headless");
                    yield new ChromeDriver(options);
                }
                case FrameworkConstants.FIREFOX -> {
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions options = getFirefoxOptions();
                    options.addArguments("--headless");
                    yield new FirefoxDriver(options);
                }
                case FrameworkConstants.EDGE -> {
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions options = getEdgeOptions();
                    options.addArguments("--headless");
                    yield new EdgeDriver(options);
                }
                default -> {
                    LogManager.warn("Headless mode not supported for browser: {}. Using Chrome.", browserType);
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = getChromeOptions();
                    options.addArguments("--headless");
                    yield new ChromeDriver(options);
                }
            };
            
            configureDriver(driver);
            
        } catch (Exception e) {
            LogManager.error(FrameworkConstants.DRIVER_INITIALIZATION_ERROR, e.getMessage());
            throw new RuntimeException("Failed to create headless driver", e);
        }
        
        return driver;
    }
} 