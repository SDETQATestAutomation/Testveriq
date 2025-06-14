# Architecture Blueprint - Test Automation Framework

## PLAN Stage Overview
**Stage**: PLAN (Detailed Architecture & Design)  
**Complexity**: Level 3 (Enterprise Features)  
**Architecture**: 5-Layer Modular Design with SOLID Principles

## Framework Architecture Layers

### Layer 1: Test Layer (BDD + TestNG)
```
┌─────────────────────────────────────────────────────────┐
│                    TEST LAYER                           │
│  ┌─────────────────────┐  ┌─────────────────────────┐   │
│  │   Feature Files     │  │   TestNG Suites         │   │
│  │  (Gherkin/BDD)      │  │  (Direct Testing)       │   │
│  └─────────────────────┘  └─────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Layer 2: Business Layer (Page Objects + Steps)
```
┌─────────────────────────────────────────────────────────┐
│                  BUSINESS LAYER                         │
│  ┌─────────────────────┐  ┌─────────────────────────┐   │
│  │   Step Definitions  │  │   Page Objects          │   │
│  │  (Cucumber Steps)   │  │  (Page Object Model)    │   │
│  └─────────────────────┘  └─────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Layer 3: Service Layer (API + Database)
```
┌─────────────────────────────────────────────────────────┐
│                  SERVICE LAYER                          │
│  ┌─────────────────────┐  ┌─────────────────────────┐   │
│  │   API Clients       │  │   Database Utilities    │   │
│  │  (RestAssured)      │  │  (JDBC/Repository)      │   │
│  └─────────────────────┘  └─────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Layer 4: Core Layer (Drivers + Config + Utils)
```
┌─────────────────────────────────────────────────────────┐
│                    CORE LAYER                           │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │
│  │ Driver Mgmt  │ │ Config Mgmt  │ │ Utility Services │ │
│  │ (Selenium)   │ │ (Properties) │ │ (Wait, Assert)   │ │
│  └──────────────┘ └──────────────┘ └──────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### Layer 5: Infrastructure Layer (Logging + Reporting + CI)
```
┌─────────────────────────────────────────────────────────┐
│               INFRASTRUCTURE LAYER                      │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ │
│  │ Log4J2       │ │ Allure       │ │ Jenkins Pipeline │ │
│  │ (Logging)    │ │ (Reporting)  │ │ (CI/CD)          │ │
│  └──────────────┘ └──────────────┘ └──────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Modular Component Design

### Module 1: Core Framework Components
```java
// Driver Management Module
├── DriverFactory.java          # Factory pattern for driver creation
├── DriverManager.java          # ThreadLocal driver management
├── BrowserConfig.java          # Browser-specific configurations
└── CloudDriverManager.java    # Selenium Grid/Cloud integration

// Base Classes Module
├── BaseTest.java               # TestNG base class with hooks
├── BasePage.java               # Page Object base class
├── BaseSteps.java              # Cucumber steps base class
└── BaseAPI.java                # API testing base class
```

### Module 2: Configuration Management
```java
// Configuration Module
├── ConfigLoader.java           # Property file loader with profiles
├── EnvironmentConfig.java      # Environment-specific settings
├── TestDataConfig.java         # Test data management
└── SecurityConfig.java         # Encrypted credential handling
```

### Module 3: Utility Services
```java
// Utility Module
├── WaitHelper.java             # Selenium wait strategies
├── ElementHelper.java          # Element interaction utilities
├── FileHelper.java             # File operations (Excel, JSON, CSV)
├── DatabaseHelper.java         # Database connectivity utilities
├── ApiHelper.java              # REST API testing utilities
└── ScreenshotHelper.java       # Screenshot capture utilities
```

### Module 4: Validation & Assertion Layer
```java
// Validation Module
├── AssertionHelper.java        # Custom assertion methods
├── SoftAssertManager.java      # Soft assertion implementation
├── ValidationHelper.java       # Data validation utilities
└── ResponseValidator.java      # API response validation
```

### Module 5: Reporting & Logging
```java
// Reporting Module
├── AllureManager.java          # Allure reporting integration
├── LogManager.java             # Log4J2 wrapper utilities
├── TestResultManager.java     # Test result aggregation
└── NotificationManager.java   # Email/Slack notifications
```

## Component Interface Contracts

### IDriverManager Interface
```java
public interface IDriverManager {
    WebDriver initializeDriver(String browserName);
    void setDriver(WebDriver driver);
    WebDriver getDriver();
    void quitDriver();
    void quitAllDrivers();
}
```

### IConfigManager Interface
```java
public interface IConfigManager {
    String getProperty(String key);
    String getProperty(String key, String defaultValue);
    void setProperty(String key, String value);
    Map<String, String> getAllProperties();
    void loadEnvironmentConfig(String environment);
}
```

### IPageBase Interface
```java
public interface IPageBase {
    void waitForPageLoad();
    boolean isPageLoaded();
    void takeScreenshot(String screenshotName);
    WebElement findElement(By locator);
    List<WebElement> findElements(By locator);
}
```

### ITestBase Interface
```java
public interface ITestBase {
    void setupTest();
    void setupClass();
    void teardownTest();
    void teardownClass();
    void captureFailureDetails(String testName);
}
```

## Design Pattern Implementation

### 1. Factory Pattern - Driver Creation
```java
public class DriverFactory {
    public static WebDriver createDriver(BrowserType browser) {
        switch (browser) {
            case CHROME: return createChromeDriver();
            case FIREFOX: return createFirefoxDriver();
            case SAFARI: return createSafariDriver();
            case EDGE: return createEdgeDriver();
            default: throw new IllegalArgumentException("Browser not supported");
        }
    }
}
```

### 2. Singleton Pattern - Configuration Management
```java
public class ConfigManager implements IConfigManager {
    private static volatile ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() { loadProperties(); }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
}
```

### 3. Builder Pattern - Test Data Construction
```java
public class TestDataBuilder {
    private Map<String, Object> testData = new HashMap<>();
    
    public TestDataBuilder withUserName(String userName) {
        testData.put("userName", userName);
        return this;
    }
    
    public TestDataBuilder withPassword(String password) {
        testData.put("password", password);
        return this;
    }
    
    public TestData build() {
        return new TestData(testData);
    }
}
```

### 4. Strategy Pattern - Wait Strategies
```java
public interface IWaitStrategy {
    WebElement waitForElement(WebDriver driver, By locator, int timeout);
}

public class ExplicitWaitStrategy implements IWaitStrategy {
    public WebElement waitForElement(WebDriver driver, By locator, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout))
            .until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
```

## Thread Safety & Parallel Execution Design

### ThreadLocal Implementation
```java
public class ThreadLocalDriverManager {
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> sessionIdThreadLocal = new ThreadLocal<>();
    
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
        sessionIdThreadLocal.set(UUID.randomUUID().toString());
    }
    
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    public static void removeDriver() {
        driverThreadLocal.remove();
        sessionIdThreadLocal.remove();
    }
}
```

### Parallel Execution Configuration
```xml
<!-- TestNG Suite Configuration -->
<suite name="ParallelTestSuite" parallel="methods" thread-count="5">
    <test name="RegressionTests">
        <classes>
            <class name="com.framework.tests.LoginTests"/>
            <class name="com.framework.tests.CheckoutTests"/>
        </classes>
    </test>
</suite>
```

## Error Handling & Recovery Patterns

### Global Exception Handler
```java
@Component
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger();
    
    public void handleTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable exception = result.getThrowable();
        
        // Log the failure
        logger.error("Test {} failed with exception: {}", testName, exception.getMessage());
        
        // Capture screenshot
        ScreenshotHelper.captureScreenshot(testName);
        
        // Attach to Allure report
        AllureManager.attachScreenshot(testName);
        
        // Send notification if critical
        if (isCriticalTest(testName)) {
            NotificationManager.sendFailureAlert(testName, exception);
        }
    }
}
```

## Security & Data Protection

### Credential Encryption
```java
public class SecureCredentialManager {
    private static final String ENCRYPTION_KEY = System.getenv("FRAMEWORK_ENCRYPTION_KEY");
    
    public static String encryptCredential(String plainText) {
        // AES encryption implementation
        return AESUtil.encrypt(plainText, ENCRYPTION_KEY);
    }
    
    public static String decryptCredential(String encryptedText) {
        // AES decryption implementation
        return AESUtil.decrypt(encryptedText, ENCRYPTION_KEY);
    }
}
```

## Performance Optimization Strategies

### Element Caching
```java
public class CachedElementLocator {
    private static final Map<String, WebElement> elementCache = new ConcurrentHashMap<>();
    
    public static WebElement findElement(By locator) {
        String locatorKey = locator.toString();
        return elementCache.computeIfAbsent(locatorKey, k -> 
            ThreadLocalDriverManager.getDriver().findElement(locator));
    }
}
```

### Smart Wait Implementation
```java
public class SmartWaitHelper {
    public static WebElement waitForClickableElement(By locator) {
        return waitForClickableElement(locator, ConfigManager.getInstance()
            .getProperty("default.timeout", "10"));
    }
    
    public static WebElement waitForClickableElement(By locator, int timeout) {
        return new WebDriverWait(ThreadLocalDriverManager.getDriver(), 
            Duration.ofSeconds(timeout))
            .until(ExpectedConditions.elementToBeClickable(locator));
    }
}
```

## Next PLAN Stage Deliverables
1. ✅ **Architecture Blueprint**: 5-layer modular design completed
2. 🔄 **Maven Structure**: Project structure and dependencies (next)
3. 🔄 **Configuration Design**: Environment management (next)
4. 🔄 **CI/CD Pipeline**: Jenkins integration (next)
5. 🔄 **Sample Implementation**: Boilerplate code (next) 