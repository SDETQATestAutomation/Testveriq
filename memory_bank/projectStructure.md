# Project Structure - Test Automation Framework

## Maven Project Structure

### Complete Directory Layout
```
TestAutomationFramework/
├── pom.xml                          # Maven configuration
├── README.md                        # Project documentation
├── .gitignore                       # Git ignore patterns
├── Jenkinsfile                      # CI/CD pipeline configuration
├── testng.xml                       # TestNG suite configuration
├── allure.properties               # Allure reporting configuration
├── log4j2.xml                      # Log4J2 configuration
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── automation/
│   │               └── framework/
│   │                   ├── config/           # Configuration management
│   │                   │   ├── ConfigManager.java
│   │                   │   ├── EnvironmentConfig.java
│   │                   │   ├── TestDataConfig.java
│   │                   │   └── SecurityConfig.java
│   │                   │
│   │                   ├── core/             # Core framework components
│   │                   │   ├── BaseTest.java
│   │                   │   ├── BasePage.java
│   │                   │   ├── BaseSteps.java
│   │                   │   └── BaseAPI.java
│   │                   │
│   │                   ├── driver/           # WebDriver management
│   │                   │   ├── DriverFactory.java
│   │                   │   ├── DriverManager.java
│   │                   │   ├── BrowserConfig.java
│   │                   │   └── CloudDriverManager.java
│   │                   │
│   │                   ├── utils/            # Utility services
│   │                   │   ├── WaitHelper.java
│   │                   │   ├── ElementHelper.java
│   │                   │   ├── FileHelper.java
│   │                   │   ├── DatabaseHelper.java
│   │                   │   ├── ApiHelper.java
│   │                   │   └── ScreenshotHelper.java
│   │                   │
│   │                   ├── validators/       # Validation and assertions
│   │                   │   ├── AssertionHelper.java
│   │                   │   ├── SoftAssertManager.java
│   │                   │   ├── ValidationHelper.java
│   │                   │   └── ResponseValidator.java
│   │                   │
│   │                   └── reporting/        # Reporting and logging
│   │                       ├── AllureManager.java
│   │                       ├── LogManager.java
│   │                       ├── TestResultManager.java
│   │                       └── NotificationManager.java
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── automation/
│       │           └── tests/
│       │               ├── hooks/            # Test hooks (Before/After)
│       │               │   ├── CucumberHooks.java
│       │               │   └── TestNGHooks.java
│       │               │
│       │               ├── pages/            # Page Object Models
│       │               │   ├── LoginPage.java
│       │               │   ├── HomePage.java
│       │               │   └── CheckoutPage.java
│       │               │
│       │               ├── steps/            # Cucumber step definitions
│       │               │   ├── LoginSteps.java
│       │               │   ├── HomeSteps.java
│       │               │   └── CheckoutSteps.java
│       │               │
│       │               ├── runners/          # Test runners
│       │               │   ├── CucumberTestRunner.java
│       │               │   └── TestNGRunner.java
│       │               │
│       │               ├── api/              # API test classes
│       │               │   ├── UserAPITests.java
│       │               │   └── ProductAPITests.java
│       │               │
│       │               └── ui/               # UI test classes
│       │                   ├── LoginTests.java
│       │                   ├── CheckoutTests.java
│       │                   └── RegressionTests.java
│       │
│       └── resources/
│           ├── config/                      # Configuration files
│           │   ├── application.properties
│           │   ├── dev.properties
│           │   ├── qa.properties
│           │   └── prod.properties
│           │
│           ├── features/                    # Cucumber feature files
│           │   ├── login.feature
│           │   ├── checkout.feature
│           │   └── regression.feature
│           │
│           ├── testdata/                    # Test data files
│           │   ├── users.json
│           │   ├── products.csv
│           │   └── testdata.xlsx
│           │
│           ├── drivers/                     # Local driver binaries
│           │   ├── chromedriver
│           │   ├── geckodriver
│           │   └── msedgedriver
│           │
│           └── schemas/                     # API schema validation
│               ├── user-schema.json
│               └── product-schema.json
│
├── target/                                 # Maven build outputs
│   ├── classes/
│   ├── test-classes/
│   ├── allure-results/                     # Allure raw results
│   └── allure-report/                      # Generated Allure report
│
├── logs/                                   # Application logs
│   ├── automation.log
│   └── test-execution.log
│
└── docs/                                   # Documentation
    ├── API-Documentation.md
    ├── User-Guide.md
    └── Troubleshooting.md
```

## Maven Configuration (pom.xml)

### Complete POM Structure
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.automation</groupId>
    <artifactId>test-automation-framework</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Enterprise Test Automation Framework</name>
    <description>Modular, scalable test automation framework using Selenium 4, TestNG, and Cucumber</description>

    <properties>
        <!-- Java Version -->
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- Framework Dependencies -->
        <selenium.version>4.15.0</selenium.version>
        <testng.version>7.8.0</testng.version>
        <cucumber.version>7.14.0</cucumber.version>
        <webdrivermanager.version>5.5.3</webdrivermanager.version>
        
        <!-- Reporting Dependencies -->
        <allure.version>2.24.0</allure.version>
        <allure.maven.version>2.12.0</allure.maven.version>
        
        <!-- Logging Dependencies -->
        <log4j.version>2.20.0</log4j.version>
        
        <!-- Utility Dependencies -->
        <jackson.version>2.15.2</jackson.version>
        <apache.poi.version>5.2.4</apache.poi.version>
        <restassured.version>5.3.2</restassured.version>
        <mysql.connector.version>8.1.0</mysql.connector.version>
        
        <!-- Build Plugin Versions -->
        <maven.surefire.version>3.1.2</maven.surefire.version>
        <maven.compiler.version>3.11.0</maven.compiler.version>
        
        <!-- Test Execution Properties -->
        <suite.file>testng.xml</suite.file>
        <browser>chrome</browser>
        <environment>qa</environment>
        <parallel.threads>5</parallel.threads>
    </properties>

    <dependencies>
        <!-- Selenium WebDriver -->
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>${selenium.version}</version>
        </dependency>

        <!-- WebDriverManager for automatic driver management -->
        <dependency>
            <groupId>io.github.bonigarcia</groupId>
            <artifactId>webdrivermanager</artifactId>
            <version>${webdrivermanager.version}</version>
        </dependency>

        <!-- TestNG -->
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
        </dependency>

        <!-- Cucumber -->
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>${cucumber.version}</version>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-testng</artifactId>
            <version>${cucumber.version}</version>
        </dependency>

        <!-- Allure Reporting -->
        <dependency>
            <groupId>io.qameta.allure</groupId>
            <artifactId>allure-testng</artifactId>
            <version>${allure.version}</version>
        </dependency>
        <dependency>
            <groupId>io.qameta.allure</groupId>
            <artifactId>allure-cucumber7-jvm</artifactId>
            <version>${allure.version}</version>
        </dependency>

        <!-- Log4J2 -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>${log4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>${log4j.version}</version>
        </dependency>

        <!-- Jackson for JSON handling -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <!-- Apache POI for Excel handling -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>${apache.poi.version}</version>
        </dependency>

        <!-- RestAssured for API testing -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${restassured.version}</version>
        </dependency>

        <!-- MySQL Connector -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>${mysql.connector.version}</version>
        </dependency>

        <!-- Apache Commons -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.13.0</version>
        </dependency>

        <!-- Gson for JSON handling (alternative) -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven.compiler.version}</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>

            <!-- Maven Surefire Plugin for TestNG -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${maven.surefire.version}</version>
                <configuration>
                    <suiteXmlFiles>
                        <suiteXmlFile>${suite.file}</suiteXmlFile>
                    </suiteXmlFiles>
                    <systemPropertyVariables>
                        <browser>${browser}</browser>
                        <environment>${environment}</environment>
                    </systemPropertyVariables>
                    <properties>
                        <property>
                            <name>dataproviderthreadcount</name>
                            <value>${parallel.threads}</value>
                        </property>
                    </properties>
                </configuration>
            </plugin>

            <!-- Allure Maven Plugin -->
            <plugin>
                <groupId>io.qameta.allure</groupId>
                <artifactId>allure-maven</artifactId>
                <version>${allure.maven.version}</version>
                <configuration>
                    <reportVersion>${allure.version}</reportVersion>
                    <resultsDirectory>${project.build.directory}/allure-results</resultsDirectory>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <!-- Maven Profiles for Different Environments -->
    <profiles>
        <!-- Development Profile -->
        <profile>
            <id>dev</id>
            <properties>
                <environment>dev</environment>
                <suite.file>src/test/resources/suites/dev-suite.xml</suite.file>
            </properties>
        </profile>

        <!-- QA Profile -->
        <profile>
            <id>qa</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <environment>qa</environment>
                <suite.file>src/test/resources/suites/qa-suite.xml</suite.file>
            </properties>
        </profile>

        <!-- Production Profile -->
        <profile>
            <id>prod</id>
            <properties>
                <environment>prod</environment>
                <suite.file>src/test/resources/suites/prod-suite.xml</suite.file>
            </properties>
        </profile>

        <!-- Parallel Execution Profile -->
        <profile>
            <id>parallel</id>
            <properties>
                <parallel.threads>10</parallel.threads>
                <suite.file>src/test/resources/suites/parallel-suite.xml</suite.file>
            </properties>
        </profile>

        <!-- Regression Testing Profile -->
        <profile>
            <id>regression</id>
            <properties>
                <suite.file>src/test/resources/suites/regression-suite.xml</suite.file>
            </properties>
        </profile>

        <!-- Smoke Testing Profile -->
        <profile>
            <id>smoke</id>
            <properties>
                <suite.file>src/test/resources/suites/smoke-suite.xml</suite.file>
            </properties>
        </profile>
    </profiles>
</project>
```

## Configuration Files Structure

### TestNG Suite Configuration (testng.xml)
```xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="AutomationTestSuite" parallel="methods" thread-count="5">
    
    <parameter name="browser" value="chrome"/>
    <parameter name="environment" value="qa"/>
    
    <test name="SmokeTests" group-by-instances="true">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.automation.tests.ui.LoginTests"/>
            <class name="com.automation.tests.ui.CheckoutTests"/>
        </classes>
    </test>
    
    <test name="RegressionTests" group-by-instances="true">
        <groups>
            <run>
                <include name="regression"/>
            </run>
        </groups>
        <classes>
            <class name="com.automation.tests.ui.RegressionTests"/>
        </classes>
    </test>
    
    <test name="APITests" group-by-instances="true">
        <groups>
            <run>
                <include name="api"/>
            </run>
        </groups>
        <classes>
            <class name="com.automation.tests.api.UserAPITests"/>
            <class name="com.automation.tests.api.ProductAPITests"/>
        </classes>
    </test>
    
</suite>
```

### Application Properties (application.properties)
```properties
# Browser Configuration
browser.default=chrome
browser.headless=false
browser.maximized=true
browser.timeout.implicit=10
browser.timeout.explicit=20
browser.timeout.page.load=30

# Environment URLs
url.base=https://demo-app.com
url.api.base=https://api.demo-app.com
url.admin=https://admin.demo-app.com

# Database Configuration
db.host=localhost
db.port=3306
db.name=testdb
db.username=testuser
db.password=encrypted_password

# Test Data Configuration
testdata.path=src/test/resources/testdata/
testdata.format.default=json
testdata.cache.enabled=true

# Reporting Configuration
reports.path=target/reports/
screenshots.path=target/screenshots/
screenshots.on.failure=true
screenshots.on.pass=false

# Logging Configuration
log.level=INFO
log.file.path=logs/automation.log
log.console.enabled=true

# Parallel Execution
parallel.threads=5
parallel.timeout.multiplier=2

# Retry Configuration
retry.failed.tests=true
retry.count=2

# Notification Configuration
notification.email.enabled=false
notification.slack.enabled=false
notification.teams.enabled=false
```

## Maven Commands & Execution

### Essential Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run all tests
mvn clean test

# Run tests with specific profile
mvn clean test -Pqa
mvn clean test -Pdev
mvn clean test -Pprod

# Run tests with parameters
mvn clean test -Dbrowser=firefox -Denvironment=qa

# Run parallel tests
mvn clean test -Pparallel

# Run specific test groups
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=regression

# Generate Allure report
mvn allure:report

# Serve Allure report
mvn allure:serve

# Run tests and generate report
mvn clean test allure:report

# Package the framework
mvn clean package

# Install to local repository
mvn clean install
```

### Environment-Specific Execution
```bash
# Development environment
mvn clean test -Pdev -Dbrowser=chrome

# QA environment with parallel execution
mvn clean test -Pqa -Pparallel -Dbrowser=firefox

# Production environment smoke tests
mvn clean test -Pprod -Dgroups=smoke

# Regression testing in QA
mvn clean test -Pqa -Pregression
```

## Next PLAN Stage Deliverables
1. ✅ **Architecture Blueprint**: 5-layer modular design completed
2. ✅ **Maven Structure**: Project structure and dependencies completed
3. 🔄 **Configuration Design**: Environment management (next)
4. 🔄 **CI/CD Pipeline**: Jenkins integration (next)
5. 🔄 **Sample Implementation**: Boilerplate code (next) 