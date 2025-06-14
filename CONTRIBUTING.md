# Contributing to Testveriq

🎉 Thank you for your interest in contributing to **Testveriq**! We're excited to collaborate with the community to make this test automation framework even better.

**Developed by**: [SDETQATestAutomation](https://github.com/SDETQATestAutomation)  
**Contact**: prashant.ranjan.qa@gmail.com  
**Repository**: https://github.com/SDETQATestAutomation/Testveriq

---

## 📋 Table of Contents

- [🤝 How to Contribute](#-how-to-contribute)
- [🔧 Development Setup](#-development-setup)
- [📝 Code Standards](#-code-standards)
- [🧪 Testing Guidelines](#-testing-guidelines)
- [📄 Documentation](#-documentation)
- [🚀 Pull Request Process](#-pull-request-process)
- [🐛 Bug Reports](#-bug-reports)
- [💡 Feature Requests](#-feature-requests)
- [📞 Getting Help](#-getting-help)

---

## 🤝 How to Contribute

We welcome contributions in many forms:

- 🐛 **Bug fixes** and improvements
- ✨ **New features** and enhancements
- 📚 **Documentation** improvements
- 🧪 **Test coverage** expansion
- 🔧 **Code quality** improvements
- 📈 **Performance** optimizations
- 🎨 **UI/UX** enhancements for reports

---

## 🔧 Development Setup

### Prerequisites
```bash
# Required Software
Java 17+ (OpenJDK or Oracle)
Maven 3.8+
Git 2.30+
IDE (IntelliJ IDEA recommended)
```

### 1. Fork and Clone
```bash
# Fork the repository on GitHub
# Then clone your fork
git clone https://github.com/YOUR_USERNAME/Testveriq.git
cd Testveriq

# Add upstream remote
git remote add upstream https://github.com/SDETQATestAutomation/Testveriq.git
```

### 2. Set Up Development Environment
```bash
# Install dependencies
mvn clean install

# Verify setup
mvn test -Dtest=SampleLoginTest

# Generate documentation
mvn javadoc:javadoc
```

### 3. IDE Configuration

#### IntelliJ IDEA
```xml
<!-- Import project as Maven project -->
<!-- Configure Code Style -->
Settings > Editor > Code Style > Java
- Use spaces: 4
- Continuation indent: 8
- Tab size: 4

<!-- Configure Inspections -->
Settings > Editor > Inspections
- Enable: Java > Probable bugs
- Enable: Java > Code style issues
- Enable: Java > Performance
```

#### Eclipse
```xml
<!-- Import as Maven project -->
<!-- Configure formatting -->
Window > Preferences > Java > Code Style > Formatter
- Apply Eclipse [built-in] formatting
- Line wrapping: 120 characters
```

---

## 📝 Code Standards

### Java Coding Standards

#### Class Structure
```java
/**
 * Brief description of the class purpose
 * 
 * This class implements [specific functionality] following [design pattern]
 * and adheres to [relevant principles].
 * 
 * Usage Example:
 * <pre>
 * {@code
 * ClassName instance = new ClassName();
 * instance.methodName();
 * }
 * </pre>
 * 
 * @author SDETQATestAutomation Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ExampleClass {
    
    // Constants (UPPER_SNAKE_CASE)
    private static final String DEFAULT_CONFIG = "application.properties";
    
    // Static variables
    private static final Logger logger = LogManager.getLogger(ExampleClass.class);
    
    // Instance variables (camelCase)
    private String configFilePath;
    private Properties properties;
    
    // Constructor
    public ExampleClass() {
        this.properties = new Properties();
        initializeDefaults();
    }
    
    // Public methods
    public void publicMethod() {
        // Implementation
    }
    
    // Private methods
    private void initializeDefaults() {
        // Implementation
    }
}
```

#### Method Standards
```java
/**
 * Brief description of what the method does
 * 
 * @param paramName Description of parameter
 * @return Description of return value
 * @throws ExceptionType When this exception is thrown
 */
public ReturnType methodName(ParameterType paramName) throws ExceptionType {
    // Input validation
    Objects.requireNonNull(paramName, "Parameter cannot be null");
    
    try {
        // Main logic
        return processParameter(paramName);
    } catch (SpecificException e) {
        logger.error("Error processing parameter: {}", paramName, e);
        throw new ExceptionType("Processing failed", e);
    }
}
```

#### Naming Conventions
- **Classes**: PascalCase (`WebDriverManager`)
- **Methods**: camelCase (`setupWebDriver`)
- **Variables**: camelCase (`driverInstance`)
- **Constants**: UPPER_SNAKE_CASE (`DEFAULT_TIMEOUT`)
- **Packages**: lowercase with dots (`com.automation.framework`)

#### Error Handling
```java
// Good: Specific exception handling
try {
    WebElement element = findElement(locator);
    element.click();
} catch (NoSuchElementException e) {
    logger.warn("Element not found: {}", locator);
    throw new FrameworkException("Element interaction failed", e);
} catch (ElementNotInteractableException e) {
    logger.warn("Element not interactable: {}", locator);
    // Retry with JavaScript click
    jsClick(locator);
}

// Avoid: Generic exception catching
try {
    // Some operation
} catch (Exception e) {
    // Too generic - avoid this
}
```

### Code Quality Checklist
- [ ] **Single Responsibility**: Each class/method has one clear purpose
- [ ] **Null Safety**: Proper null checks and validation
- [ ] **Exception Handling**: Specific and meaningful error handling
- [ ] **Logging**: Appropriate log levels and messages
- [ ] **JavaDoc**: Complete documentation for public methods
- [ ] **Thread Safety**: Consider concurrent access if applicable
- [ ] **Resource Management**: Proper cleanup of resources
- [ ] **Performance**: Efficient algorithms and data structures

---

## 🧪 Testing Guidelines

### Test Structure
```java
@Test(groups = {"smoke", "login"})
@SmartRetry(retryCount = 2)
@Description("Verify user can login with valid credentials")
public void testValidLogin() {
    // Arrange
    String username = "valid_user";
    String password = "valid_password";
    
    // Act
    loginPage.enterUsername(username);
    loginPage.enterPassword(password);
    loginPage.clickLogin();
    
    // Assert
    Assert.assertTrue(homePage.isDisplayed(), "Home page should be displayed after login");
    Assert.assertEquals(homePage.getWelcomeMessage(), "Welcome, " + username);
}
```

### Test Categories
- **Smoke Tests**: Basic functionality verification
- **Regression Tests**: Comprehensive feature testing
- **Integration Tests**: Component interaction testing
- **Performance Tests**: Load and stress testing
- **Security Tests**: Authentication and authorization

### Test Data Management
```java
// Use data providers for parameterized tests
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return new Object[][] {
        {"user1", "pass1", true},
        {"user2", "pass2", false},
        {"admin", "admin123", true}
    };
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password, boolean expectedResult) {
    // Test implementation
}
```

---

## 📄 Documentation

### Required Documentation
1. **JavaDoc**: All public methods and classes
2. **README Updates**: For new features
3. **Code Comments**: Complex logic explanation
4. **Test Documentation**: Test case descriptions

### Documentation Standards
```java
/**
 * Performs intelligent element click with retry mechanism
 * 
 * This method implements a smart clicking strategy that:
 * 1. Waits for element to be clickable
 * 2. Attempts regular click
 * 3. Falls back to JavaScript click if needed
 * 4. Captures screenshot on failure
 * 
 * @param locator The element locator to click
 * @param timeoutSeconds Maximum wait time in seconds
 * @throws FrameworkException If element cannot be clicked after all attempts
 * 
 * @since 1.0.0
 */
public void smartClick(By locator, int timeoutSeconds) throws FrameworkException {
    // Implementation
}
```

---

## 🚀 Pull Request Process

### 1. Create Feature Branch
```bash
# Create and switch to feature branch
git checkout -b feature/add-mobile-support
git checkout -b bugfix/fix-retry-logic
git checkout -b docs/update-readme
```

### 2. Development Workflow
```bash
# Make changes
git add .
git commit -m "feat: add mobile device support for iOS and Android"

# Keep branch updated
git fetch upstream
git rebase upstream/main

# Push to your fork
git push origin feature/add-mobile-support
```

### 3. Commit Message Format
```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

#### Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

#### Examples:
```
feat(driver): add support for mobile browsers
fix(retry): resolve infinite retry loop issue
docs(readme): update installation instructions
test(login): add edge case tests for authentication
```

### 4. Pull Request Checklist
- [ ] **Code Quality**: Follows coding standards
- [ ] **Tests**: New tests added and existing tests pass
- [ ] **Documentation**: Updated relevant documentation
- [ ] **No Breaking Changes**: Backward compatibility maintained
- [ ] **Clean History**: Commits are squashed if needed
- [ ] **Description**: Clear PR description with context

### 5. PR Template
```markdown
## 📝 Description
Brief description of changes made.

## 🔄 Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Refactoring
- [ ] Other (please describe)

## 🧪 Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed
- [ ] All tests pass

## 📋 Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes
```

---

## 🐛 Bug Reports

### Before Reporting
1. **Search existing issues** to avoid duplicates
2. **Update to latest version** to see if bug is fixed
3. **Minimal reproduction** case prepared

### Bug Report Template
```markdown
## 🐛 Bug Description
Clear description of the bug.

## 🔄 Steps to Reproduce
1. Go to '...'
2. Click on '...'
3. Execute '...'
4. See error

## ✅ Expected Behavior
What should happen.

## ❌ Actual Behavior
What actually happens.

## 🖥️ Environment
- OS: [e.g., Windows 10, macOS 12.1]
- Java Version: [e.g., OpenJDK 17.0.1]
- Browser: [e.g., Chrome 96.0]
- Framework Version: [e.g., 1.0.0]

## 📋 Additional Context
Screenshots, logs, or other relevant information.
```

---

## 💡 Feature Requests

### Feature Request Template
```markdown
## 🚀 Feature Request
Brief description of the feature.

## 🎯 Problem Statement
What problem does this solve?

## 💡 Proposed Solution
How should this work?

## 🔄 Alternatives Considered
Other approaches considered.

## 📈 Additional Benefits
Other benefits this feature provides.
```

---

## 📞 Getting Help

### Communication Channels
- **GitHub Issues**: Technical questions and discussions
- **Email**: prashant.ranjan.qa@gmail.com
- **YouTube**: [SDET-QA Test Automation](https://www.youtube.com/@sdet-qatestautomation7214)

### Before Asking for Help
1. **Check Documentation**: README, Developer Guide, Architecture Overview
2. **Search Issues**: Existing GitHub issues and discussions
3. **Review Examples**: Sample test implementations
4. **Check Logs**: Framework and test execution logs

### Question Template
```markdown
## ❓ Question
Clear question description.

## 🎯 What I'm Trying to Do
Goal or objective.

## 🔄 What I've Tried
Steps already attempted.

## 📋 Environment
- Framework Version:
- Java Version:
- Operating System:
```

---

## 🏆 Recognition

Contributors will be recognized in:
- **README.md**: Contributors section
- **CHANGELOG.md**: Release notes
- **GitHub Releases**: Release descriptions
- **Social Media**: Feature announcements

---

## 📜 Code of Conduct

We are committed to providing a welcoming and inclusive environment. Please:

- **Be Respectful**: Treat everyone with respect and kindness
- **Be Constructive**: Provide helpful feedback and suggestions
- **Be Patient**: Help others learn and grow
- **Be Inclusive**: Welcome people of all backgrounds and experience levels

---

## 🎓 Learning Resources

### Framework Development
- **Clean Architecture**: Robert C. Martin
- **Design Patterns**: Gang of Four
- **Test Automation**: Alan Richardson
- **Java Best Practices**: Joshua Bloch - Effective Java

### Video Tutorials
- **YouTube Channel**: [SDET-QA Test Automation](https://www.youtube.com/@sdet-qatestautomation7214)
- **Framework Deep Dives**: Architecture and implementation details
- **Best Practices**: Coding standards and patterns
- **Live Coding**: Real-time development sessions

---

**Thank you for contributing to Testveriq!** 🚀

*Built with ❤️ by the SDETQATestAutomation Community* 