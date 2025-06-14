package com.automation.examples;

import com.automation.framework.base.BaseTest;
import com.automation.framework.utils.ElementActions;
import com.automation.framework.utils.WaitFactory;
import com.automation.framework.utils.ScreenshotUtil;
import com.automation.framework.retry.SmartRetry;
import com.automation.framework.exceptions.FrameworkException;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import io.qameta.allure.*;
import io.qameta.allure.testng.Tag;

/**
 * Sample Login Test demonstrating framework capabilities
 * 
 * This test class showcases:
 * - Basic test structure and inheritance from BaseTest
 * - Smart retry mechanism with annotations
 * - Element interactions using ElementActions
 * - Wait strategies with WaitFactory
 * - Screenshot capture for evidence
 * - Allure reporting integration
 * - Data-driven testing with TestNG DataProvider
 * - Proper test organization and grouping
 * 
 * @author SDETQATestAutomation Team
 * @version 1.0.0
 */
@Epic("User Authentication")
@Feature("Login Functionality")
public class SampleLoginTest extends BaseTest {
    
    // Page elements - In real implementation, use Page Object Model
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("loginButton");
    private final By welcomeMessage = By.id("welcomeMessage");
    private final By errorMessage = By.className("error-message");
    private final By logoutButton = By.id("logoutButton");
    private final By forgotPasswordLink = By.linkText("Forgot Password?");
    
    // Test data constants
    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_PASSWORD = "testpass123";
    private static final String INVALID_USERNAME = "invaliduser";
    private static final String INVALID_PASSWORD = "wrongpass";
    
    /**
     * Test setup method - executed before each test method
     * Demonstrates custom test setup with navigation
     */
    @BeforeMethod
    @Step("Navigate to login page")
    public void navigateToLoginPage() {
        String loginUrl = getBaseUrl() + "/login";
        navigateTo(loginUrl);
        
        // Verify login page loaded correctly
        WaitFactory.waitForElementVisible(usernameField, 10);
        Assert.assertTrue(ElementActions.isElementDisplayed(usernameField, "username field"), 
            "Username field should be visible on login page");
    }
    
    /**
     * Test 1: Basic successful login test
     * Demonstrates: Basic element interactions, assertions, Allure annotations
     */
    @Test(groups = {"smoke", "regression"}, priority = 1)
    @Story("Successful Login")
    @Description("Verify that a user can successfully login with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("login")
    @Tag("smoke")
    public void testSuccessfulLogin() {
        // Test implementation with Allure steps
        enterCredentials(VALID_USERNAME, VALID_PASSWORD);
        clickLoginButton();
        verifySuccessfulLogin();
    }
    
    /**
     * Test 2: Invalid credentials test with retry mechanism
     * Demonstrates: SmartRetry annotation, error handling, soft assertions
     */
    @Test(groups = {"regression"}, priority = 2)
    @Story("Invalid Login")
    @Description("Verify that appropriate error message is shown for invalid credentials")
    @Severity(SeverityLevel.NORMAL)
    @SmartRetry(
        maxRetries = 2,
        delayBetweenRetries = 1500,
        retryOn = {TimeoutException.class, NoSuchElementException.class},
        progressiveDelay = true,
        captureScreenshotOnRetry = true,
        reason = "Error message might take time to appear"
    )
    public void testInvalidCredentialsLogin() {
        enterCredentials(INVALID_USERNAME, INVALID_PASSWORD);
        clickLoginButton();
        
        // Wait for error message to appear
        WaitFactory.waitForElementVisible(errorMessage, 5);
        
        // Use soft assertions to check multiple conditions
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(ElementActions.isElementDisplayed(errorMessage, "error message"),
            "Error message should be displayed for invalid credentials");
        
        String actualErrorText = ElementActions.smartGetText(errorMessage, "error message");
        softAssert.assertTrue(actualErrorText.contains("Invalid"),
            "Error message should contain 'Invalid' text");
        
        // Verify user is still on login page
        softAssert.assertTrue(ElementActions.isElementDisplayed(loginButton, "login button"),
            "Login button should still be visible after failed login");
        
        softAssert.assertAll();
    }
    
    /**
     * Test 3: Data-driven test with multiple credential combinations
     * Demonstrates: TestNG DataProvider, parameterized testing
     */
    @Test(dataProvider = "loginCredentials", groups = {"regression"})
    @Story("Multiple Login Scenarios")
    @Description("Test login with various credential combinations")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithMultipleCredentials(String username, String password, 
                                               boolean expectedSuccess, String description) {
        
        // Add description to Allure report
        Allure.parameter("Username", username);
        Allure.parameter("Password", password.replaceAll(".", "*")); // Mask password
        Allure.parameter("Expected Result", expectedSuccess ? "Success" : "Failure");
        Allure.description(description);
        
        enterCredentials(username, password);
        clickLoginButton();
        
        if (expectedSuccess) {
            verifySuccessfulLogin();
            logout(); // Clean up for next iteration
        } else {
            verifyLoginFailure();
        }
    }
    
    /**
     * Test 4: Empty fields validation
     * Demonstrates: Field validation, multiple assertions
     */
    @Test(groups = {"regression"}, priority = 3)
    @Story("Field Validation")
    @Description("Verify validation messages for empty username and password fields")
    @Severity(SeverityLevel.MINOR)
    public void testEmptyFieldsValidation() {
        // Test empty username
        ElementActions.smartSendKeys(passwordField, VALID_PASSWORD, "password field");
        clickLoginButton();
        
        String validationMessage = getValidationMessage(usernameField);
        Assert.assertFalse(validationMessage.isEmpty(),
            "Username field should show validation message when empty");
        
        // Clear fields and test empty password
        clearField(passwordField);
        ElementActions.smartSendKeys(usernameField, VALID_USERNAME, "username field");
        clickLoginButton();
        
        validationMessage = getValidationMessage(passwordField);
        Assert.assertFalse(validationMessage.isEmpty(),
            "Password field should show validation message when empty");
    }
    
    /**
     * Test 5: UI element verification test
     * Demonstrates: Element state verification, screenshot capture
     */
    @Test(groups = {"ui", "regression"})
    @Story("UI Elements")
    @Description("Verify all required UI elements are present on login page")
    @Severity(SeverityLevel.MINOR)
    public void testLoginPageUIElements() {
        // Capture screenshot of login page
        String screenshotPath = ScreenshotUtil.captureScreenshot("login_page_ui");
        Allure.addAttachment("Login Page UI", "Screenshot captured: " + screenshotPath);
        
        // Verify all expected elements are present
        SoftAssert softAssert = new SoftAssert();
        
        softAssert.assertTrue(ElementActions.isElementDisplayed(usernameField, "username field"),
            "Username field should be visible");
        softAssert.assertTrue(ElementActions.isElementDisplayed(passwordField, "password field"),
            "Password field should be visible");
        softAssert.assertTrue(ElementActions.isElementDisplayed(loginButton, "login button"),
            "Login button should be visible");
        softAssert.assertTrue(ElementActions.isElementDisplayed(forgotPasswordLink, "forgot password link"),
            "Forgot password link should be visible");
        
        softAssert.assertAll();
    }
    
    /**
     * Test 6: Login with special characters
     * Demonstrates: Edge case testing, proper error handling
     */
    @Test(groups = {"edge-cases"})
    @Story("Edge Cases")
    @Description("Test login behavior with special characters in credentials")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithSpecialCharacters() {
        String specialUsername = "test@#$%user";
        String specialPassword = "pass!@#$%^&*()";
        
        try {
            enterCredentials(specialUsername, specialPassword);
            clickLoginButton();
            
            // Expect failure or success based on application behavior
            // This test helps identify how the application handles special characters
            if (ElementActions.isElementDisplayed(errorMessage, "error message")) {
                String errorText = ElementActions.smartGetText(errorMessage, "error message");
                Assert.assertFalse(errorText.isEmpty(),
                    "Error message should not be empty for special characters");
            }
            
        } catch (Exception e) {
            // Log the exception for analysis
            ScreenshotUtil.captureFailureScreenshot("special_characters_login");
            throw new FrameworkException("Login with special characters failed", e);
        }
    }
    
    // =============================================================================
    // HELPER METHODS (Allure Steps)
    // =============================================================================
    
    @Step("Enter credentials: {username} / ****")
    private void enterCredentials(String username, String password) {
        clearField(usernameField);
        ElementActions.smartSendKeys(usernameField, username, "username field");
        
        clearField(passwordField);
        ElementActions.smartSendKeys(passwordField, password, "password field");
    }
    
    @Step("Click login button")
    private void clickLoginButton() {
        ElementActions.smartClick(loginButton, "login button");
    }
    
    @Step("Verify successful login")
    private void verifySuccessfulLogin() {
        WaitFactory.waitForElementVisible(welcomeMessage, 10);
        
        Assert.assertTrue(ElementActions.isElementDisplayed(welcomeMessage, "welcome message"),
            "Welcome message should be displayed after successful login");
        
        String welcomeText = ElementActions.smartGetText(welcomeMessage, "welcome message");
        Assert.assertTrue(welcomeText.contains("Welcome") || welcomeText.contains("Hello"),
            "Welcome message should contain greeting text");
        
        // Verify logout button is available
        Assert.assertTrue(ElementActions.isElementDisplayed(logoutButton, "logout button"),
            "Logout button should be visible after successful login");
    }
    
    @Step("Verify login failure")
    private void verifyLoginFailure() {
        // Wait for error message or ensure we're still on login page
        try {
            WaitFactory.waitForElementVisible(errorMessage, 5);
            Assert.assertTrue(ElementActions.isElementDisplayed(errorMessage, "error message"),
                "Error message should be displayed for invalid credentials");
        } catch (TimeoutException e) {
            // If no error message, verify we're still on login page
            Assert.assertTrue(ElementActions.isElementDisplayed(loginButton, "login button"),
                "Should remain on login page after failed login attempt");
        }
    }
    
    @Step("Logout from application")
    private void logout() {
        if (ElementActions.isElementDisplayed(logoutButton, "logout button")) {
            ElementActions.smartClick(logoutButton, "logout button");
            WaitFactory.waitForElementVisible(loginButton, 10);
        }
    }
    
    @Step("Get validation message for field")
    private String getValidationMessage(By element) {
        try {
            return ElementActions.smartGetAttribute(element, "validationMessage", "validation message");
        } catch (Exception e) {
            return "";
        }
    }
    
    @Step("Clear field")
    private void clearField(By element) {
        try {
            // Find element and clear it using JavaScript to ensure it's cleared
            WaitFactory.waitForElementVisible(element, 5);
            // Framework will handle clearing in smartSendKeys, so this is just a placeholder
        } catch (Exception e) {
            // Ignore clearing errors
        }
    }
    
    // =============================================================================
    // DATA PROVIDERS
    // =============================================================================
    
    /**
     * Data provider for multiple login credential combinations
     */
    @DataProvider(name = "loginCredentials")
    public Object[][] getLoginCredentials() {
        return new Object[][]{
            {VALID_USERNAME, VALID_PASSWORD, true, "Valid credentials should allow login"},
            {INVALID_USERNAME, VALID_PASSWORD, false, "Invalid username should prevent login"},
            {VALID_USERNAME, INVALID_PASSWORD, false, "Invalid password should prevent login"},
            {"", VALID_PASSWORD, false, "Empty username should prevent login"},
            {VALID_USERNAME, "", false, "Empty password should prevent login"},
            {"", "", false, "Empty credentials should prevent login"},
            {"admin", "admin", false, "Default admin credentials should be disabled"},
            {"test@example.com", VALID_PASSWORD, false, "Email format username test"}
        };
    }
    
    // =============================================================================
    // TEST LIFECYCLE METHODS
    // =============================================================================
    
    @AfterMethod
    public void cleanupAfterTest() {
        // Ensure we're logged out after each test
        try {
            if (ElementActions.isElementDisplayed(logoutButton, "logout button")) {
                logout();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @AfterClass
    public void printTestSummary() {
        // This will be called after all tests in this class complete
        System.out.println("=".repeat(60));
        System.out.println("Sample Login Test Class Execution Complete");
        System.out.println("=".repeat(60));
    }
} 