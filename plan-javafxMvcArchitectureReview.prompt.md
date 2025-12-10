# Comprehensive JavaFX MVC Architecture Review and Refactoring Strategy

This is a **brutal but constructive** technical analysis of your Travel & Tours Management System. The codebase has
foundational structure but suffers from critical architectural violations, SQL injection vulnerabilities, resource
leaks, and lacks proper separation of concerns. Below is a detailed breakdown with actionable refactoring steps.

## 🔥 **CRITICAL PROBLEMS** (Must Fix Immediately)

### 1. SQL Injection Vulnerabilities Everywhere

- `AuthController.java` lines 67-68: Raw string concatenation in `validateLogin()` — a hacker's paradise
- Line 106-112: `registerEmployee()` also concatenates user input directly into SQL
- **Every single database query** in your app is vulnerable
- This is a **production-blocking security disaster**

```java
// VULNERABLE CODE (DO NOT USE):
String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = '" + usernameTextField.getText() + "' AND Password = '" + passwordPasswordField.getText() + "';";
```

### 2. Database Connection Leaks

- `DatabaseConnection.java`: Returns raw `Connection` with no lifecycle management
- **Zero** instances of `connection.close()`, `statement.close()`, or `resultSet.close()` anywhere
- Controllers call `getConnection()` repeatedly, creating new connections every time
- Your app will exhaust the database connection pool under moderate load

### 3. Hardcoded Credentials in Source Code

- `DatabaseConnection.java` lines 13-15: Username `jerico` and password `password@12345` are **committed to Git**
- Database name, IP address (192.168.254.149) — all hardcoded
- This violates every security best practice

### 4. Controllers Are Doing EVERYTHING (God Class Anti-Pattern)

- `AuthController.java`: Handles UI events, SQL queries, scene navigation, and business logic
- `MainLayoutController.java` & `MainLayoutController2.java` are **identical duplicates** (lines 70-160)
- Controllers contain SQL strings, direct JDBC calls, data transformation — this is NOT MVC

### 5. Blocking Database Calls on JavaFX Application Thread

- Every `validateLogin()`, `displayTotalCustomers()`, `buildTable()` call blocks the UI thread
- No use of `Task`, `Service`, or background threading anywhere
- Your app will freeze during any database operation

---

## ⚠️ **DESIGN SMELLS & ARCHITECTURAL RISKS**

### 6. Missing Service/Repository Layer

- No separation between data access and business logic
- Controllers directly execute raw SQL — impossible to test or swap database
- No abstraction: `EmployeeController`, `MainLayoutController` all duplicate database logic

### 7. Model Classes Are JavaFX Property Wrappers, Not Domain Models

- `Employee.java`: Uses `SimpleStringProperty` — tightly couples your domain to JavaFX
- Storing booleans as `String` (`isManager`, `isActive`) — type safety nightmare
- `Customer.java` and `Booking.java` are **empty shells**
- Models should be pure POJOs; wrap them in ViewModels for UI binding

### 8. Scene Navigation Is a Mess

- `SceneController.java`: Base class with `setCenter()` but no clear contract
- Controllers inherit from `SceneController` but also manually load scenes via `FXMLLoader`
- No centralized navigation manager — every controller loads FXML paths as magic strings
- Scene switching in `AuthController.java` line 79 creates new `FXMLLoader` without controller dependency injection

### 9. No Validation Layer

- `loginButton()` checks if fields are blank (line 52) but no actual validation
- No email format validation, password strength rules, or contact number format
- Registration allows duplicate emails, no password confirmation check

### 10. Code Duplication

- `MainLayoutController.java` and `MainLayoutController2.java` are 99% identical
- Same database query patterns copy-pasted across `displayTotalCustomers()`, `displayOngoingTrips()`, etc.
- Every method creates `new DatabaseConnection()` and repeats the same try-catch pattern

### 11. Poor Exception Handling

- Generic `catch (Exception e)` everywhere
- Just `e.printStackTrace()` — no logging framework, no user feedback (except in a few cases)
- Swallowing exceptions means silent failures

### 12. Magic Strings and Hardcoded Values

- FXML paths like `"/com/cht/TravelAndToursManagement/view/Login-view.fxml"` scattered across multiple files
- SQL column names as raw strings (`"name"`, `"email"`, etc.)
- Status values like `"pending"`, `"confirmed"` hardcoded in queries

### 13. Unused Imports and Dead Code

- `MainLayoutController.java` line 18: Imports `logger` from MySQL connector (unused)
- `AuthController.java`: Multiple unused FXML fields (`nameLabel`, `emailLabel`, etc.)
- `ClientApp.java`: Commented-out scene manager code

### 14. No Dependency Injection

- Controllers manually instantiate `DatabaseConnection`, tightly coupling to infrastructure
- No way to inject mock databases for testing
- Impossible to swap implementations without editing every controller

### 15. Naming Inconsistencies

- `DatabaseConnection.databaseLink` (public field!) vs. `getConnection()` method
- `TableContainer` (Pascal case) vs. `colName` (camel case) in `EmployeeController.java`
- Method names like `loginButton()` (should be `onLoginButtonClicked`)

---

## ✅ **GOOD PRACTICES OBSERVED** (Don't Throw Everything Away)

1. **Maven Project Structure** — Using Maven with proper POM dependencies
2. **FXML Separation** — Views are in `.fxml` files, not hardcoded in Java
3. **Module Descriptors** — You have `module-info.java` (though it needs cleanup)
4. **Basic MVC Attempt** — You have separate `model`, `view`, `controller` packages (even if boundaries are violated)
5. **JavaFX Properties in Model** — Using `SimpleStringProperty` shows understanding of observable values (though
   misapplied)

---

## 🛠 **RECOMMENDED REFACTORING STEPS** (Priority Order)

### **Phase 1: Stop the Bleeding (Security & Stability)**

#### 1. Replace All SQL Concatenation with PreparedStatements

- Rewrite every query in `AuthController.java`, `MainLayoutController.java`, `EmployeeController.java`
- Use `PreparedStatement` with `?` placeholders

**Example Fix:**

```java
// BEFORE (VULNERABLE):
String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = '" + email + "' AND Password = '" + password + "';";

// AFTER (SECURE):
String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = ? AND Password = ?";
PreparedStatement pstmt = connectDB.prepareStatement(verifyLogin);
pstmt.

setString(1,email);
pstmt.

setString(2,password);

ResultSet rs = pstmt.executeQuery();
```

#### 2. Implement Resource Management with Try-With-Resources

- Wrap all `Connection`, `Statement`, `ResultSet` in try-with-resources blocks
- Add `AutoCloseable` handling

**Example Fix:**

```java
try(Connection conn = connectNow.getConnection();
PreparedStatement pstmt = conn.prepareStatement(query);
ResultSet rs = pstmt.executeQuery()){
        // Process results
        }catch(
SQLException e){
        // Handle exception
        }
```

#### 3. Externalize Configuration

- Move database credentials to `application.properties` or environment variables
- Use `ResourceBundle` or a config loader

**Create `application.properties`:**

```properties
db.url=jdbc:mysql://192.168.254.149:3306/cht_updated
db.username=${DB_USERNAME}
db.password=${DB_PASSWORD}
db.driver=com.mysql.cj.jdbc.Driver
```

---

### **Phase 2: Introduce Service Layer**

#### 4. Create Service Classes

- `AuthenticationService` — handle login/registration logic
- `EmployeeService` — employee CRUD operations
- `BookingService` — booking management
- `DashboardService` — aggregate statistics

**Example Service:**

```java
public class AuthenticationService {
    private final EmployeeRepository employeeRepository;

    public AuthenticationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public boolean authenticate(String email, String password) {
        // Hash password, validate, etc.
        return employeeRepository.validateCredentials(email, password);
    }

    public Employee register(Employee employee) throws ValidationException {
        validateEmployee(employee);
        return employeeRepository.save(employee);
    }
}
```

#### 5. Create Repository/DAO Layer

- `EmployeeRepository` — all employee database operations
- `CustomerRepository`, `BookingRepository`
- Each repository wraps JDBC logic with prepared statements

**Example Repository Interface:**

```java
public interface EmployeeRepository {
    Optional<Employee> findByEmail(String email);

    List<Employee> findAll();

    Employee save(Employee employee);

    void delete(Long id);

    boolean validateCredentials(String email, String password);
}
```

#### 6. Refactor Controllers to Use Services

- Controllers should only handle UI events and call service methods
- Remove all SQL from controllers
- Pass data to services, receive results

**Example Refactored Controller:**

```java
public class AuthController {
    private final AuthenticationService authService;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Label loginMessageLabel;

    // Constructor injection (requires controller factory)
    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @FXML
    public void onLoginButtonClicked() {
        String email = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (email.isBlank() || password.isBlank()) {
            loginMessageLabel.setText("Username or Password is empty!");
            return;
        }

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() {
                return authService.authenticate(email, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            if (loginTask.getValue()) {
                navigationService.navigateTo(Route.DASHBOARD);
            } else {
                loginMessageLabel.setText("Invalid credentials");
            }
        });

        new Thread(loginTask).start();
    }
}
```

---

### **Phase 3: Fix Models and ViewModels**

#### 7. Separate Domain Models from View Models

- Create POJO versions: `Employee` (domain), `EmployeeViewModel` (JavaFX properties)
- Use mappers to convert between them
- Domain models should be database-agnostic

**Domain Model (Pure POJO):**

```java
public class Employee {
    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private boolean isManager;
    private boolean isActive;

    // Constructor, getters, setters
    // No JavaFX dependencies
}
```

**ViewModel (JavaFX Binding):**

```java
public class EmployeeViewModel {
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty contactNumber;
    private final SimpleBooleanProperty isManager;
    private final SimpleBooleanProperty isActive;

    public EmployeeViewModel(Employee employee) {
        this.name = new SimpleStringProperty(employee.getName());
        this.email = new SimpleStringProperty(employee.getEmail());
        // ... map other fields
    }

    public StringProperty nameProperty() {
        return name;
    }
    // ... other property accessors
}
```

#### 8. Implement Proper Model Classes

- Fill out `Customer.java` and `Booking.java`
- Add validation methods to models
- Use proper types (`boolean` not `String` for flags)

---

### **Phase 4: Threading & Performance**

#### 9. Wrap Database Calls in JavaFX Task

- Create `Task<List<Employee>>` for `buildTable()`
- Use `Service<T>` for reusable background operations
- Update UI with `Platform.runLater()` or Task progress callbacks

**Example Background Task:**

```java
public void loadEmployees() {
    Task<List<Employee>> task = new Task<>() {
        @Override
        protected List<Employee> call() {
            return employeeService.getAllEmployees();
        }
    };

    task.setOnSucceeded(e -> {
        List<Employee> employees = task.getValue();
        ObservableList<EmployeeViewModel> viewModels = employees.stream()
                .map(EmployeeViewModel::new)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableView.setItems(viewModels);
    });

    task.setOnFailed(e -> {
        // Show error dialog
    });

    new Thread(task).start();
}
```

#### 10. Add Connection Pooling

- Replace manual connection creation with HikariCP or Apache DBCP
- Configure pool size, timeouts

**HikariCP Configuration:**

```java
public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

---

### **Phase 5: Navigation & Architecture**

#### 11. Centralize Scene Management

- Create `NavigationService` or `SceneManager` singleton (or DI-injected)
- Define routes as enum or constants (`Route.LOGIN`, `Route.DASHBOARD`)
- Handle FXML loading, controller injection, scene transitions in one place

**Navigation Service:**

```java
public class NavigationService {
    private final Stage primaryStage;
    private final Map<Route, String> routeMap;
    private final ControllerFactory controllerFactory;

    public NavigationService(Stage primaryStage, ControllerFactory factory) {
        this.primaryStage = primaryStage;
        this.controllerFactory = factory;
        this.routeMap = initializeRoutes();
    }

    public void navigateTo(Route route) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(routeMap.get(route)));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new NavigationException("Failed to navigate to " + route, e);
        }
    }
}

public enum Route {
    LOGIN, DASHBOARD, EMPLOYEE, BOOKING
}
```

#### 12. Implement Dependency Injection

- Use a lightweight DI framework (Guice, Spring, or manual constructor injection)
- Inject services into controllers via constructors
- Use a controller factory for FXML loading

**Controller Factory:**

```java
public class ControllerFactory implements Callback<Class<?>, Object> {
    private final Map<Class<?>, Object> controllers = new HashMap<>();

    public void registerController(Class<?> controllerClass, Object instance) {
        controllers.put(controllerClass, instance);
    }

    @Override
    public Object call(Class<?> controllerClass) {
        return controllers.get(controllerClass);
    }
}
```

#### 13. Remove Duplicate Controllers

- Merge `MainLayoutController.java` and `MainLayoutController2.java`
- Extract common logic into base class or shared service

---

### **Phase 6: Polish & Best Practices**

#### 14. Add Logging

- Replace `e.printStackTrace()` with SLF4J + Logback
- Log at appropriate levels (INFO, WARN, ERROR)

**Add Dependencies:**

```xml

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
</dependency>
<dependency>
<groupId>ch.qos.logback</groupId>
<artifactId>logback-classic</artifactId>
<version>1.4.11</version>
</dependency>
```

**Usage:**

```java
private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

try{
        // code
        }catch(
SQLException e){
        logger.

error("Database error during login",e);

showErrorDialog("Login failed. Please try again.");
}
```

#### 15. Input Validation

- Add validators for email, phone, password strength
- Show validation errors in UI before attempting database operations

**Validation Utils:**

```java
public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,15}");
    }
}
```

#### 16. Constants & Enums

- Create `FXMLPaths` class for view paths
- Create `BookingStatus` enum (`PENDING`, `CONFIRMED`, `CANCELLED`)
- Create `SQLQueries` class or inline as constants in repositories

**Constants:**

```java
public final class FXMLPaths {
    private FXMLPaths() {
    } // Prevent instantiation

    public static final String LOGIN = "/com/cht/TravelAndToursManagement/view/Login-view.fxml";
    public static final String DASHBOARD = "/com/cht/TravelAndToursManagement/view/MainLayout-view.fxml";
    public static final String EMPLOYEE = "/com/cht/TravelAndToursManagement/view/Employee-view.fxml";
}

public enum BookingStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
```

#### 17. Unit & Integration Tests

- Test services with mock repositories
- Test repositories with H2 in-memory database
- Use TestFX for UI testing

**Example Test:**

```java

@Test
public void testAuthenticateWithValidCredentials() {
    // Arrange
    EmployeeRepository mockRepo = mock(EmployeeRepository.class);
    when(mockRepo.validateCredentials("test@test.com", "password"))
            .thenReturn(true);
    AuthenticationService service = new AuthenticationService(mockRepo);

    // Act
    boolean result = service.authenticate("test@test.com", "password");

    // Assert
    assertTrue(result);
    verify(mockRepo).validateCredentials("test@test.com", "password");
}
```

---

## 🏗 **SUGGESTED FOLDER STRUCTURE**

```
src/main/java/com/cht/TravelAndToursManagement/
├── client/
│   ├── ClientApp.java
│   └── config/
│       ├── DatabaseConfig.java          // Replaces DatabaseConnection
│       └── AppConfig.java               // Centralized configuration
│
├── controller/                           // UI Controllers (thin)
│   ├── AuthController.java              // Only UI event handling
│   ├── DashboardController.java         // Merged MainLayout controllers
│   ├── EmployeeController.java
│   └── BookingController.java
│
├── service/                              // Business Logic Layer
│   ├── AuthenticationService.java
│   ├── EmployeeService.java
│   ├── CustomerService.java
│   ├── BookingService.java
│   └── DashboardService.java
│
├── repository/                           // Data Access Layer (DAO)
│   ├── EmployeeRepository.java
│   ├── CustomerRepository.java
│   ├── BookingRepository.java
│   └── impl/                             // JDBC implementations
│       ├── JdbcEmployeeRepository.java
│       └── ...
│
├── model/                                // Domain Models (POJOs)
│   ├── Employee.java                     // No JavaFX dependencies
│   ├── Customer.java
│   ├── Booking.java
│   └── enums/
│       ├── BookingStatus.java
│       └── UserRole.java
│
├── viewmodel/                            // JavaFX-specific models
│   ├── EmployeeViewModel.java           // With SimpleStringProperty
│   └── ...
│
├── navigation/                           // Scene management
│   ├── NavigationService.java
│   ├── Route.java                        // Enum of routes
│   └── FXMLPaths.java                    // Constants
│
├── util/                                 // Utilities
│   ├── ValidationUtils.java
│   ├── ConnectionManager.java           // Connection pooling
│   └── TaskHelper.java                  // JavaFX Task utilities
│
└── exception/                            // Custom exceptions
    ├── AuthenticationException.java
    └── DatabaseException.java

src/main/resources/
├── application.properties                // Externalized config
├── logback.xml                           // Logging config
└── com/cht/TravelAndToursManagement/view/
    └── [FXML files]
```

---

## 📌 **FINAL ARCHITECTURE ADVICE**

### Current State

You have a monolithic, controller-centric JavaFX app with MVC in name only. Controllers are fat, models are anemic, and
there's no clear separation of concerns.

### Target State

Layered architecture with:

- **Presentation Layer** (Controllers) — Handle UI events only
- **Service Layer** (Business Logic) — Orchestrate operations, validation
- **Data Access Layer** (Repositories) — Encapsulate JDBC/database logic
- **Domain Layer** (Models) — Pure business entities

### Migration Strategy

1. Start with **security fixes** (PreparedStatements, resource cleanup) — 1-2 days
2. Extract **repository layer** — wrap existing SQL in repository methods — 2-3 days
3. Extract **service layer** — move logic from controllers to services — 3-4 days
4. Refactor **models/viewmodels** — separate concerns — 2 days
5. Add **threading** for database operations — 1-2 days
6. Centralize **navigation** — 1 day
7. Add **DI framework** — 2-3 days

**Total Estimated Effort:** 2-3 weeks of focused refactoring

### Key Pattern Recommendations

- **Repository Pattern** for data access abstraction
- **Service Layer Pattern** for business logic
- **Dependency Injection** for loose coupling and testability
- **Observer Pattern** (built into JavaFX properties) for reactive UI updates
- **Command Pattern** (optional) for undo/redo in booking forms
- **Factory Pattern** for controller creation with injected dependencies

### Testing Strategy

- Mock repositories in service tests
- Use H2 for repository integration tests
- TestFX for end-to-end UI tests

### Don't Feel Discouraged

This is a learning project, and you've actually built something functional. The structure shows you understand basic
concepts. Now it's time to apply enterprise-grade patterns. The refactoring is substantial but absolutely achievable.

### One Last Thing

**Delete `MainLayoutController2.java` immediately.** Duplicate code is technical debt that will haunt you. If you need
different behavior, use polymorphism or composition, not copy-paste.

---

## Summary

This plan provides a roadmap to transform your codebase from a security liability into a maintainable, testable,
professional JavaFX application. Focus on security fixes first (Phase 1), then gradually introduce proper architecture
layers. Each phase builds on the previous one, allowing you to refactor incrementally without breaking the entire
application.

The key is to start small, test frequently, and commit changes in logical chunks. Good luck with the refactoring!

