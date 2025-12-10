# JavaFX MVC Architecture Review - CHT Travel & Tours Management System

## Deep Technical Review & Brutal Feedback

Based on comprehensive analysis of your JavaFX Travel & Tours Management System codebase, here are my findings organized
by severity and category.

---

## 🔥 CRITICAL PROBLEMS

### 1. **MVC Architecture Violation - Controllers Doing Data Access**

**SEVERITY: CRITICAL**

Your controllers are directly executing SQL queries, completely bypassing the Repository pattern you attempted to
implement.

**Evidence:**

- `AuthController.validateLogin()` - Lines 109-134: Raw SQL in controller
- `AuthController.registerEmployee()` - Lines 147-165: Direct database INSERT
- `MainLayoutController` - ALL dashboard methods contain SQL queries
- `EmployeeController.buildTable()` - Lines 51-67: SQL query execution in controller

**Why This is Terrible:**

- Violates Single Responsibility Principle
- Makes controllers **impossible to unit test** without database
- Duplicates data access logic across controllers
- No transaction management
- SQL injection vulnerability in `validateLogin()` (executeQuery called BEFORE setString)

**The Kicker:** You created `EmployeeRepository` interface but **NEVER IMPLEMENTED IT**. You also injected
`AuthenticationService` into `AuthController` but then ignored it completely in favor of raw SQL.

```java
// Line 88 in AuthController - YOU USE THE SERVICE HERE
if(loginTask.getValue()){
        NavigationService.

navigateTo(Route.DASHBOARD);
}

// But Line 109 in validateLogin() - YOU IGNORE IT AND WRITE RAW SQL
String verifyLogin = "SELECT COUNT(1) FROM Employee WHERE Email = ? AND Password = ?";
try(
Connection connectDB = DatabaseConfig.getConnection(); ...){
```

This is schizophrenic architecture.

---

### 2. **Broken Navigation System**

**SEVERITY: CRITICAL**

Your navigation system is fundamentally broken and won't run.

**Problems:**

a) **Static Method That Doesn't Exist:**

```java
// AuthController line 88
NavigationService.navigateTo(Route.DASHBOARD); // STATIC CALL
```

But `NavigationService.navigateTo()` is an **instance method**, not static. This won't compile or will throw NPE.

b) **Missing Method Implementation:**

```java
// NavigationService line 20
this.routeMap =

initializedRoutes(); // METHOD DOESN'T EXIST
```

The `initializedRoutes()` method is **never defined**. Your app can't start.

c) **NavigationService Never Used:**
Your `ClientApp.start()` creates a `NavigationService` but never calls it. The app doesn't show any initial scene.

```java

@Override
public void start(Stage primaryStage) throws IOException {
    NavigationService navigationService = new NavigationService(primaryStage, controllerFactory);
    // ??? Now what? Nothing happens
}
```

---

### 3. **Dependency Injection Framework Abandoned Mid-Flight**

**SEVERITY: CRITICAL**

You started building a `ControllerFactory` for dependency injection but then **completely abandoned it**.

**The Problem:**

All your FXML files hardcode controllers:

```xml
fx:controller="com.cht.TravelAndToursManagement.client.controller.AuthController"
```

This means JavaFX creates controllers using the **default no-arg constructor**, but your controllers require
dependencies:

```java
public AuthController(AuthenticationService authService, NavigationService navigationService) {
    // Constructor requires dependencies
}
```

**Result:** `FXMLLoader` will fail with `javafx.fxml.LoadException` because it can't construct your controllers.

Your `ControllerFactory` exists but:

1. Controllers are never registered with it
2. FXML files don't use it (hardcoded fx:controller)
3. No `setControllerFactory()` called on FXMLLoader in most places

This is half-baked dependency injection that will crash on startup.

---

### 4. **SQL Injection Vulnerability**

**SEVERITY: CRITICAL - SECURITY**

```java
// AuthController.validateLogin() - Lines 109-113
try(Connection connectDB = DatabaseConfig.getConnection();
PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
ResultSet resultSet = preparedStatement.executeQuery()){ // EXECUTED HERE
        preparedStatement.

setString(1,email);  // SET PARAMETERS AFTER EXECUTION
    preparedStatement.

setString(2,password);
```

You call `executeQuery()` **BEFORE** binding parameters. This means:

1. The query executes with placeholders, likely returning no results
2. Parameters never actually get bound
3. If you "fix" this by concatenating strings, you open SQL injection

---

### 5. **Model Contains JavaFX UI Dependencies**

**SEVERITY: HIGH**

Your domain `Employee` model should be a pure POJO but returns JavaFX properties:

```java
// Employee.java - But called from EmployeeController line 66
colManager.setCellValueFactory(cellData ->cellData.

getValue().

isManager());
```

Wait, this is calling `Employee.isManager()` which returns `boolean`, but you're using it in `setCellValueFactory` which
expects `ObservableValue<String>`.

**This code doesn't even work.** Did you test this?

Looking deeper:

```java
// EmployeeController line 63
colName.setCellValueFactory(cellData ->cellData.

getValue().

nameProperty());
```

But `Employee` doesn't have `nameProperty()` - it only has `getName()` returning `String`.

**You created `EmployeeViewModel`** with proper JavaFX properties, but then **NEVER USED IT**. The controller binds
`TableView<Employee>` instead of `TableView<EmployeeViewModel>`.

This is a mess of confusion between domain models and view models.

---

### 6. **HikariCP Misconfiguration**

**SEVERITY: HIGH**

```java
// DatabaseConfig static block
HikariConfig config = new HikariConfig();
config.

setJdbcUrl(ConfigLoader.get("db.url"));
// ... configuration
// BUT WHERE IS: dataSource = new HikariDataSource(config); ???
```

You configure HikariCP but never instantiate the `dataSource`. Then you call:

```java
public static Connection getConnection() throws SQLException {
    return dataSource.getConnection(); // NPE - dataSource is null
}
```

**Guaranteed NullPointerException** on first database call.

---

## ⚠️ DESIGN SMELLS & RISKS

### 7. **SceneController Anti-Pattern**

```java
public class SceneController {
    @FXML
    public BorderPane contentArea;

    protected void setCenter(String fxmlPath) { ...}
}
```

**Problems:**

- Base class with `@FXML` fields is an anti-pattern
- `contentArea` will be null unless FXML injects it
- Controllers inherit this and call `setCenter()` assuming `contentArea` exists
- Tight coupling to BorderPane layout structure
- No cleanup of previously loaded scenes (memory leak)

**Better Approach:** Composition over inheritance. Create a `NavigationService` that controllers use, not extend.

---

### 8. **God Controller Anti-Pattern**

`MainLayoutController` is doing WAY too much:

- Dashboard display logic
- Multiple database queries
- Navigation routing
- Scene management

**Lines of responsibility:**

- `displayTotalCustomers()` - Data access (should be in repository)
- `displayOngoingTrips()` - Data access (should be in repository)
- `displayUpcomingTrips()` - Data access (should be in repository)
- `displayCompletedTrips()` - Data access (should be in repository)
- `goToDashboard()`, `goToDashboard2()`, `goToEmployee()` - Navigation (should be in NavigationService)

This controller has **at least 5 different responsibilities**.

---

### 9. **Duplicate Code Smell**

All four dashboard display methods are **identical** except for:

- SQL query
- Column name
- Label field

```java
public void displayOngoingTrips() {
    String query = "SELECT COUNT(*) AS ongoing FROM booking WHERE status = 'pending'";
    // ... 15 lines of identical code
}

public void displayUpcomingTrips() {
    String query = "SELECT COUNT(*) AS upcoming FROM booking WHERE status = 'pending'";
    // ... 15 lines of SAME code
}
```

**SAME QUERY for ongoing and upcoming?** Copy-paste error.

This screams for extraction:

```java
private void displayCount(String query, String column, Label label) { ...}
```

---

### 10. **Exception Handling Disaster**

```java
catch(Exception e){
        e.

printStackTrace();  // Anti-pattern
    System.out.

println("Error on Building Data");  // Useless message
}
```

**Problems:**

- `e.printStackTrace()` goes to stderr, not your logging framework
- You have SLF4J + Logback configured but don't use it
- Catching generic `Exception` is lazy
- No user feedback on errors
- `System.out.println` instead of logger

---

### 11. **Threading Inconsistency**

```java
// AuthController.onLoginButtonClicked() - GOOD, uses Task
Task<Boolean> loginTask = new Task<>() { ...
        };
new

Thread(loginTask).

start();

// But AuthController.validateLogin() - BAD, blocks FX thread
try(
Connection connectDB = DatabaseConfig.getConnection(); ...){
        // Synchronous DB call on FX Application Thread
        }
```

Pick a pattern and stick with it. Every database call should be async.

---

### 12. **Magic Strings Everywhere**

```java
"/com/cht/TravelAndToursManagement/view/Employee-view.fxml"
        "/com/cht/TravelAndToursManagement/view/MainLayout-view.fxml"
        "/com/cht/TravelAndToursManagement/view/Register-view.fxml"
```

You created `FXMLPaths` constants class but:

1. Only defined 3 constants
2. Controllers ignore it and use magic strings
3. `FXMLPaths` randomly contains `BookingStatus` enum (????)

---

### 13. **Missing Domain Models**

```java
// Booking.java
public class Booking {
}

// Customer.java  
public class Customer {
}
```

Empty placeholder classes. Your database schema references these but classes are not implemented. Technical debt or
work-in-progress?

---

### 14. **Package Structure Confusion**

**Good:**

- Separation of concerns (controller, model, service, repository, config)
- Navigation package for routing logic

**Bad:**

- `viewmodel` package with one file that's never used
- `FXMLPaths` contains routing constants AND unrelated `BookingStatus` enum
- No `dto` package for data transfer objects
- No `exception` package for custom exceptions
- `utils` has one validation class (not really a "utils" package)

---

### 15. **Maven Configuration Contradiction**

```xml

<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>

<plugin>
<artifactId>maven-compiler-plugin</artifactId>
<configuration>
    <release>11</release>
    <source>25</source>  <!-- WHAT? Java 25 doesn't exist -->
    <target>25</target>
</configuration>
</plugin>
```

Contradictory Java versions. Pick one: Java 11 or 21 (not 25).

---

### 16. **Unused Dependency Injection**

```java
public AuthController(AuthenticationService authService, NavigationService navigationService) {
    this.authService = authService;
    this.navigationService = navigationService;
}
```

You inject `authService` but then write a completely separate `validateLogin()` method that:

- Ignores the service
- Reimplements authentication with raw SQL
- Duplicates the logic

**Why inject dependencies you don't use?**

---

### 17. **Missing ValidationException Class**

```java
// AuthenticationService line 18
throw new ValidationException(...); // CLASS DOESN'T EXIST
```

Referenced but never defined. Code won't compile.

---

### 18. **Inconsistent Naming Conventions**

- `contentArea` (camelCase) vs `TableContainer` (PascalCase) - both are FXML fields
- `validateLogin()` vs `registerEmployee()` - inconsistent verb/noun ordering
- `goToEmployee()` vs `addBooking()` - inconsistent navigation naming
- `displayTotalCustomers()` - too verbose for private method

---

### 19. **No Error Boundaries**

If any controller initialization fails:

- No global exception handler
- Application crashes silently or shows cryptic error
- No user-friendly error dialog
- No logging of initialization failures

---

### 20. **Potential Memory Leaks**

**Leak Sources:**

1. `SceneController.setCenter()` loads new scenes but never cleans up old ones
2. No explicit cleanup of TableView cell factories
3. Database connections in static methods with no lifecycle management
4. HikariCP never properly shut down (no shutdown hook)
5. No `@PreDestroy` or cleanup methods in controllers

---

## ✅ GOOD PRACTICES OBSERVED

Let's be fair - you did some things right:

1. **✅ Used HikariCP** for connection pooling (even if misconfigured)
2. **✅ Separated Model from View** (domain `Employee` is separate from `EmployeeViewModel`)
3. **✅ Created Repository Interface** (even if not implemented)
4. **✅ Service Layer Pattern** attempted with `AuthenticationService`
5. **✅ Used SLF4J + Logback** for logging framework (even if not consistently used)
6. **✅ Properties-based Configuration** with `ConfigLoader`
7. **✅ Try-with-resources** for proper JDBC resource management
8. **✅ JavaFX Task** for async operations in one place
9. **✅ Validation Utils** separated from business logic
10. **✅ Enum for Routes** instead of magic strings
11. **✅ Password stored as PasswordField** (though not hashed!)
12. **✅ Package structure** follows standard Java conventions
13. **✅ Maven modular project** structure
14. **✅ Test infrastructure** set up (H2, TestFX, Mockito)

The **intent** is good - you understand the patterns. **Execution** is incomplete.

---

## 🛠 RECOMMENDED REFACTORING STEPS

### Priority 1: Fix Critical Breakages

#### Step 1.1: Implement Repository Pattern

```java

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final DataSource dataSource;

    public EmployeeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        String sql = "SELECT * FROM Employee WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmployee(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find employee", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        String sql = "SELECT COUNT(1) FROM Employee WHERE email = ? AND password = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password); // FIXME: Should hash password
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to validate credentials", e);
        }
    }

    // Implement other methods...
}
```

#### Step 1.2: Create Service Layer for Dashboard

```java
public class DashboardService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public DashboardService(BookingRepository bookingRepository,
                            CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    public DashboardStats getStats() {
        return new DashboardStats(
                customerRepository.count(),
                bookingRepository.countByStatus("ongoing"),
                bookingRepository.countByStatus("upcoming"),
                bookingRepository.countByStatus("confirmed")
        );
    }
}

public record DashboardStats(int totalCustomers, int ongoingTrips,
                             int upcomingTrips, int completedTrips) {
}
```

#### Step 1.3: Fix HikariCP Initialization

```java
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigLoader.get("db.url"));
            config.setUsername(ConfigLoader.get("db.username"));
            config.setPassword(ConfigLoader.get("db.password"));
            config.setMaximumPoolSize(ConfigLoader.getInt("db.maximumPoolSize"));
            config.setMinimumIdle(ConfigLoader.getInt("db.minimumIdle"));
            config.setMaxLifetime(ConfigLoader.getInt("db.maxLifetime"));

            dataSource = new HikariDataSource(config); // FIX: Actually create it

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (dataSource != null && !dataSource.isClosed()) {
                    dataSource.close();
                }
            }));
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConfig() {
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

#### Step 1.4: Implement Missing NavigationService Method

```java
public class NavigationService {
    private final Stage primaryStage;
    private final Map<Route, String> routeMap;
    private final ControllerFactory controllerFactory;

    public NavigationService(Stage primaryStage, ControllerFactory factory) {
        this.primaryStage = primaryStage;
        this.controllerFactory = factory;
        this.routeMap = initializedRoutes();

        // Show initial scene
        navigateTo(Route.LOGIN);
    }

    private Map<Route, String> initializedRoutes() {
        return Map.of(
                Route.LOGIN, FXMLPaths.LOGIN,
                Route.DASHBOARD, FXMLPaths.DASHBOARD,
                Route.EMPLOYEE, FXMLPaths.EMPLOYEE,
                Route.BOOKING, FXMLPaths.BOOKING
        );
    }

    public void navigateTo(Route route) {
        try {
            String fxmlPath = routeMap.get(route);
            if (fxmlPath == null) {
                throw new NavigationException("No route mapping for " + route);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new NavigationException("Failed to navigate to " + route, e);
        }
    }
}
```

#### Step 1.5: Remove fx:controller from FXML Files

```xml
<!-- Before -->
<BorderPane fx:id="contentArea" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.cht.TravelAndToursManagement.client.controller.AuthController">

    <!-- After -->
    <BorderPane fx:id="contentArea" xmlns:fx="http://javafx.com/fxml/1">
```

Do this for **ALL FXML files**.

#### Step 1.6: Wire Up Dependency Injection in ClientApp

```java
public class ClientApp extends Application {
    private NavigationService navigationService;
    private ControllerFactory controllerFactory;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create infrastructure
            DataSource dataSource = DatabaseConfig.getDataSource();

            // Create repositories
            EmployeeRepository employeeRepository = new EmployeeRepositoryImpl(dataSource);
            BookingRepository bookingRepository = new BookingRepositoryImpl(dataSource);
            CustomerRepository customerRepository = new CustomerRepositoryImpl(dataSource);

            // Create services
            AuthenticationService authService = new AuthenticationService(employeeRepository);
            DashboardService dashboardService = new DashboardService(bookingRepository, customerRepository);

            // Create controller factory
            controllerFactory = new ControllerFactory();
            navigationService = new NavigationService(primaryStage, controllerFactory);

            // Register controllers with dependencies
            controllerFactory.registerController(
                    AuthController.class,
                    new AuthController(authService, navigationService)
            );
            controllerFactory.registerController(
                    MainLayoutController.class,
                    new MainLayoutController(dashboardService, navigationService)
            );
            controllerFactory.registerController(
                    EmployeeController.class,
                    new EmployeeController(employeeRepository, navigationService)
            );

            // Start navigation (shows login screen)
            navigationService.navigateTo(Route.LOGIN);

            primaryStage.setTitle("CHT Travel & Tours");

        } catch (Exception e) {
            showErrorDialog("Application Initialization Failed", e);
            Platform.exit();
        }
    }

    private void showErrorDialog(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
```

---

### Priority 2: Refactor Controllers

#### Step 2.1: Clean Up AuthController

```java
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authService;
    private final NavigationService navigationService;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private Button loginButton;

    public AuthController(AuthenticationService authService,
                          NavigationService navigationService) {
        this.authService = authService;
        this.navigationService = navigationService;
    }

    @FXML
    public void onLoginButtonClicked() {
        String email = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (!validateInput(email, password)) {
            return;
        }

        loginButton.setDisable(true);
        loginMessageLabel.setText("Logging in...");

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() {
                return authService.authenticate(email, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            loginButton.setDisable(false);
            if (loginTask.getValue()) {
                navigationService.navigateTo(Route.DASHBOARD);
            } else {
                loginMessageLabel.setText("Invalid credentials");
            }
        });

        loginTask.setOnFailed(event -> {
            loginButton.setDisable(false);
            Throwable exception = loginTask.getException();
            logger.error("Login failed", exception);
            loginMessageLabel.setText("Login failed: " + exception.getMessage());
        });

        new Thread(loginTask).start();
    }

    private boolean validateInput(String email, String password) {
        if (email.isBlank() || password.isBlank()) {
            loginMessageLabel.setText("Email and password are required");
            return false;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            loginMessageLabel.setText("Invalid email format");
            return false;
        }
        return true;
    }

    @FXML
    public void onRegisterButtonClicked() {
        navigationService.navigateTo(Route.REGISTER);
    }
}
```

#### Step 2.2: Refactor MainLayoutController

```java
public class MainLayoutController {
    private static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    private final DashboardService dashboardService;
    private final NavigationService navigationService;

    @FXML
    private Label totalCustomer;
    @FXML
    private Label ongoingTrips;
    @FXML
    private Label upcomingTrips;
    @FXML
    private Label completedTrips;

    public MainLayoutController(DashboardService dashboardService,
                                NavigationService navigationService) {
        this.dashboardService = dashboardService;
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        Task<DashboardStats> statsTask = new Task<>() {
            @Override
            protected DashboardStats call() {
                return dashboardService.getStats();
            }
        };

        statsTask.setOnSucceeded(event -> {
            DashboardStats stats = statsTask.getValue();
            totalCustomer.setText(String.valueOf(stats.totalCustomers()));
            ongoingTrips.setText(String.valueOf(stats.ongoingTrips()));
            upcomingTrips.setText(String.valueOf(stats.upcomingTrips()));
            completedTrips.setText(String.valueOf(stats.completedTrips()));
        });

        statsTask.setOnFailed(event -> {
            logger.error("Failed to load dashboard stats", statsTask.getException());
            showError("Failed to load dashboard data");
        });

        new Thread(statsTask).start();
    }

    @FXML
    public void goToEmployee() {
        navigationService.navigateTo(Route.EMPLOYEE);
    }

    @FXML
    public void addBooking() {
        navigationService.navigateTo(Route.BOOKING);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
```

#### Step 2.3: Fix EmployeeController to Use ViewModel

```java
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeRepository employeeRepository;
    private final NavigationService navigationService;

    @FXML
    private TableView<EmployeeViewModel> TableContainer;
    @FXML
    private TableColumn<EmployeeViewModel, String> colName;
    @FXML
    private TableColumn<EmployeeViewModel, String> colEmail;
    @FXML
    private TableColumn<EmployeeViewModel, String> colContact;
    @FXML
    private TableColumn<EmployeeViewModel, Boolean> colManager;
    @FXML
    private TableColumn<EmployeeViewModel, Boolean> colActive;

    public EmployeeController(EmployeeRepository employeeRepository,
                              NavigationService navigationService) {
        this.employeeRepository = employeeRepository;
        this.navigationService = navigationService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadEmployees();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colContact.setCellValueFactory(cellData -> cellData.getValue().contactNumberProperty());
        colManager.setCellValueFactory(cellData -> cellData.getValue().isManagerProperty());
        colActive.setCellValueFactory(cellData -> cellData.getValue().isActiveProperty());
    }

    private void loadEmployees() {
        Task<ObservableList<EmployeeViewModel>> loadTask = new Task<>() {
            @Override
            protected ObservableList<EmployeeViewModel> call() {
                List<Employee> employees = employeeRepository.findAll();
                return employees.stream()
                        .map(EmployeeViewModel::new)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            }
        };

        loadTask.setOnSucceeded(event -> {
            TableContainer.setItems(loadTask.getValue());
        });

        loadTask.setOnFailed(event -> {
            logger.error("Failed to load employees", loadTask.getException());
            showError("Failed to load employee data");
        });

        new Thread(loadTask).start();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
```

---

### Priority 3: Improve Architecture

#### Step 3.1: Add Exception Hierarchy

```java
// Base exception
public class TravelManagementException extends RuntimeException {
    public TravelManagementException(String message) {
        super(message);
    }

    public TravelManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Specific exceptions
public class DataAccessException extends TravelManagementException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class ValidationException extends TravelManagementException {
    public ValidationException(String message) {
        super(message);
    }
}

public class NavigationException extends TravelManagementException {
    public NavigationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### Step 3.2: Implement Password Hashing

```java
public class PasswordService {
    private static final int BCRYPT_ROUNDS = 12;

    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

// Update AuthenticationService
public class AuthenticationService {
    private final EmployeeRepository employeeRepository;
    private final PasswordService passwordService;

    public AuthenticationService(EmployeeRepository employeeRepository,
                                 PasswordService passwordService) {
        this.employeeRepository = employeeRepository;
        this.passwordService = passwordService;
    }

    public boolean authenticate(String email, String password) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        return employee.isPresent() &&
                passwordService.verifyPassword(password, employee.get().getPassword());
    }
}
```

Add to pom.xml:

```xml

<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

#### Step 3.3: Add Event Bus for Controller Communication

```java
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(handler);
    }

    public <T> void publish(T event) {
        Class<?> eventType = event.getClass();
        List<Consumer<?>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            handlers.forEach(handler -> {
                @SuppressWarnings("unchecked")
                Consumer<T> typedHandler = (Consumer<T>) handler;
                typedHandler.accept(event);
            });
        }
    }
}

// Example events
public record LoginSuccessEvent(Employee employee) {
}

public record BookingCreatedEvent(Booking booking) {
}

// Usage in controller
eventBus.

subscribe(LoginSuccessEvent .class, event ->{
        // Update UI with logged-in user
        });
```

#### Step 3.4: Consider Spring Boot Migration

For a cleaner architecture, consider migrating to Spring Boot:

```xml

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<dependencies>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
</dependencies>
```

This gives you:

- Automatic dependency injection with `@Autowired`
- Transaction management with `@Transactional`
- Built-in DataSource configuration
- Better testing support

---

### Priority 4: Code Quality Improvements

#### Step 4.1: Replace All printStackTrace

```bash
# Find all occurrences
grep -r "printStackTrace" src/

# Replace with proper logging
catch (SQLException e) {
    logger.error("Database operation failed", e);
    throw new DataAccessException("Failed to save employee", e);
}
```

#### Step 4.2: Extract Constants

```java
public final class FXMLPaths {
    private FXMLPaths() {
    }

    private static final String VIEW_BASE = "/com/cht/TravelAndToursManagement/view/";

    public static final String LOGIN = VIEW_BASE + "Login-view.fxml";
    public static final String REGISTER = VIEW_BASE + "Register-view.fxml";
    public static final String DASHBOARD = VIEW_BASE + "MainLayout-view.fxml";
    public static final String EMPLOYEE = VIEW_BASE + "Employee-view.fxml";
    public static final String BOOKING = VIEW_BASE + "AddBooking1-view.fxml";
}

public final class SQLQueries {
    private SQLQueries() {
    }

    public static final String FIND_EMPLOYEE_BY_EMAIL =
            "SELECT * FROM Employee WHERE email = ?";
    public static final String COUNT_CUSTOMERS =
            "SELECT COUNT(*) FROM client";
    public static final String COUNT_BOOKINGS_BY_STATUS =
            "SELECT COUNT(*) FROM booking WHERE status = ?";
}
```

#### Step 4.3: Add Input Validation

```java
public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("Password must contain a digit");
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("Phone number is required");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Invalid phone number format");
        }
    }
}
```

#### Step 4.4: Fix Maven Configuration

```xml

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <javafx.version>21</javafx.version>
</properties>

<build>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
            <source>17</source>
            <target>17</target>
        </configuration>
    </plugin>
</plugins>
</build>
```

---

## 🏗 SUGGESTED FOLDER STRUCTURE

Here's a clean, scalable package structure:

```
com.cht.TravelAndToursManagement.client/
│
├── ClientApp.java                          # Application entry point
│
├── config/                                 # Configuration
│   ├── ConfigLoader.java
│   ├── DatabaseConfig.java
│   └── ApplicationConfig.java              # Wire dependencies
│
├── model/                                  # Domain models (POJOs)
│   ├── domain/
│   │   ├── Employee.java
│   │   ├── Customer.java
│   │   ├── Booking.java
│   │   ├── TourPackage.java
│   │   └── Trip.java
│   │
│   └── enums/
│       ├── BookingStatus.java
│       ├── TripStatus.java
│       └── UserRole.java
│
├── dto/                                    # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── BookingRequest.java
│   └── DashboardStats.java
│
├── repository/                             # Data access layer
│   ├── EmployeeRepository.java             # Interface
│   ├── CustomerRepository.java
│   ├── BookingRepository.java
│   ├── TripRepository.java
│   │
│   └── impl/                               # Implementations
│       ├── EmployeeRepositoryImpl.java
│       ├── CustomerRepositoryImpl.java
│       ├── BookingRepositoryImpl.java
│       └── TripRepositoryImpl.java
│
├── service/                                # Business logic
│   ├── AuthenticationService.java
│   ├── PasswordService.java
│   ├── EmployeeService.java
│   ├── CustomerService.java
│   ├── BookingService.java
│   ├── DashboardService.java
│   └── TripService.java
│
├── controller/                             # JavaFX Controllers (thin)
│   ├── auth/
│   │   ├── LoginController.java
│   │   └── RegisterController.java
│   │
│   ├── dashboard/
│   │   └── DashboardController.java
│   │
│   ├── employee/
│   │   ├── EmployeeListController.java
│   │   └── EmployeeFormController.java
│   │
│   ├── customer/
│   │   ├── CustomerListController.java
│   │   └── CustomerFormController.java
│   │
│   └── booking/
│       ├── BookingWizardController.java
│       ├── BookingStep1Controller.java
│       ├── BookingStep2Controller.java
│       └── BookingStep3Controller.java
│
├── viewmodel/                              # ViewModels for JavaFX binding
│   ├── EmployeeViewModel.java
│   ├── CustomerViewModel.java
│   ├── BookingViewModel.java
│   └── TripViewModel.java
│
├── navigation/                             # Navigation/Routing
│   ├── NavigationService.java
│   ├── ControllerFactory.java
│   ├── Route.java
│   ├── FXMLPaths.java
│   └── NavigationException.java
│
├── event/                                  # Event-driven communication
│   ├── EventBus.java
│   │
│   └── events/
│       ├── LoginSuccessEvent.java
│       ├── BookingCreatedEvent.java
│       └── EmployeeUpdatedEvent.java
│
├── exception/                              # Custom exceptions
│   ├── TravelManagementException.java
│   ├── DataAccessException.java
│   ├── ValidationException.java
│   ├── AuthenticationException.java
│   └── BusinessLogicException.java
│
├── util/                                   # Utilities
│   ├── ValidationUtils.java
│   ├── DateUtils.java
│   ├── FormatUtils.java
│   └── FXMLUtils.java
│
└── component/                              # Reusable UI components
    ├── LoadingSpinner.java
    ├── ErrorDialog.java
    ├── ConfirmationDialog.java
    └── NotificationManager.java
```

**Resources Structure:**

```
resources/
│
├── application.properties                  # Configuration
├── logback.xml                             # Logging config
│
├── com/cht/TravelAndToursManagement/view/
│   ├── auth/
│   │   ├── login-view.fxml
│   │   └── register-view.fxml
│   │
│   ├── dashboard/
│   │   └── dashboard-view.fxml
│   │
│   ├── employee/
│   │   ├── employee-list-view.fxml
│   │   └── employee-form-view.fxml
│   │
│   ├── customer/
│   │   ├── customer-list-view.fxml
│   │   └── customer-form-view.fxml
│   │
│   ├── booking/
│   │   ├── booking-step1-view.fxml
│   │   ├── booking-step2-view.fxml
│   │   └── booking-step3-view.fxml
│   │
│   └── layouts/
│       ├── main-layout.fxml
│       └── sidebar-layout.fxml
│
├── css/
│   ├── application.css
│   ├── dark-theme.css
│   └── light-theme.css
│
└── images/
    ├── logo.png
    └── icons/
```

---

## 📌 FINAL ARCHITECTURE ADVICE

### What You Got Right (Keep This)

1. **Layered Architecture Intent**: Model → Repository → Service → Controller is the right direction
2. **Separation of Concerns**: Distinct packages for different responsibilities
3. **Configuration Management**: Properties file for external config
4. **Async Operations**: Understanding of JavaFX Task for background work
5. **Connection Pooling**: HikariCP is the right choice

### What You Must Fix Immediately

1. **Complete the Repository Pattern**: Implement all repositories, remove SQL from controllers
2. **Fix Dependency Injection**: Either finish manual DI or adopt Spring
3. **Separate Domain from UI**: Keep JavaFX properties in ViewModels only
4. **Implement Navigation Properly**: Make NavigationService actually work
5. **Add Exception Handling**: Global error handling strategy
6. **Hash Passwords**: Never store plain text passwords
7. **Fix SQL Injection**: Bind parameters BEFORE execution

### Architectural Evolution Path

**Phase 1: Stabilization (1-2 weeks)**

- Fix all critical breakages listed above
- Make the app actually run end-to-end
- Complete repository implementations
- Remove all SQL from controllers

**Phase 2: Refactoring (2-3 weeks)**

- Introduce proper service layer for all entities
- Implement ViewModels consistently
- Add comprehensive validation
- Improve error handling

**Phase 3: Enhancement (2-3 weeks)**

- Add event bus for decoupled communication
- Implement proper authentication/authorization
- Add caching where appropriate
- Performance optimization

**Phase 4: Migration (Optional, 2-4 weeks)**

- Consider migrating to Spring Boot
- Add Spring Data JPA for ORM
- Implement REST API layer for potential web frontend
- Add transaction management

### Design Patterns to Apply

1. **Repository Pattern** ✅ (Started, needs completion)
2. **Service Layer Pattern** ✅ (Partially done)
3. **Factory Pattern** ✅ (ControllerFactory exists)
4. **ViewModel Pattern** ⚠️ (Exists but not used)
5. **Observer Pattern** ❌ (Add EventBus)
6. **Command Pattern** ❌ (For undo/redo actions)
7. **Strategy Pattern** ❌ (For different booking types)
8. **Builder Pattern** ❌ (For complex object creation)
9. **Singleton Pattern** ⚠️ (DatabaseConfig - use carefully)
10. **Dependency Injection** ⚠️ (Half-baked - fix or use Spring)

### Testing Strategy

Your test infrastructure is set up but likely not working due to architectural issues:

1. **Unit Tests**: Test services and repositories with mocked dependencies
2. **Integration Tests**: Test database operations with H2
3. **UI Tests**: Use TestFX for controller testing
4. **End-to-End Tests**: Test complete user workflows

**Critical**: Fix the architecture first, then tests will be possible.

### Performance Considerations

1. **Database Connection Pooling**: Fix HikariCP initialization
2. **Async All The Things**: Every DB call should be in Task
3. **Lazy Loading**: Don't load all data upfront
4. **Caching**: Cache dashboard stats, refresh periodically
5. **Batch Operations**: Use batch inserts/updates for multiple rows
6. **Prepared Statement Reuse**: Consider caching prepared statements

### Security Checklist

- [ ] Hash passwords with BCrypt
- [ ] Fix SQL injection vulnerability
- [ ] Validate all user input
- [ ] Implement role-based access control
- [ ] Sanitize error messages (don't leak stack traces to UI)
- [ ] Add session management
- [ ] Implement logout functionality
- [ ] Add audit logging for sensitive operations

### Maintainability Score: 3/10

**Why Low:**

- Architectural inconsistency (patterns started but not completed)
- High coupling (controllers directly access database)
- Low testability (can't test without database)
- Code duplication (SQL queries, error handling)
- Magic strings everywhere
- Inconsistent error handling

**After Refactoring: Potential 8/10**

---

## Summary: The Brutal Truth

Your code shows **understanding of good patterns** but **incomplete execution**. You started implementing:

- Repository pattern (interface only)
- Service layer (one class)
- Dependency injection (half-working)
- Navigation system (broken)
- ViewModel pattern (unused)

Then you **gave up halfway** and fell back to:

- SQL in controllers
- Static database calls
- Hardcoded FXML controllers
- Copy-pasted code

**This is worse than not knowing the patterns at all**, because now you have:

- **Complexity without benefits**
- **Two competing architectures** (the intended one and the actual one)
- **Dead code** (unused interfaces and classes)
- **Confusion** about which approach to follow

### What To Do

**Option A: Complete the Architecture (Recommended)**
Follow the refactoring steps above. Finish what you started. Make your intended architecture actually work.

**Option B: Simplify Drastically**
If this is a school project and time is limited, **remove the half-baked patterns** and go with a simpler approach:

- Remove unused repository interfaces
- Remove unused ViewModels
- Remove broken NavigationService
- Keep controllers simple with direct SQL
- Focus on making it work, not being perfect

**Option C: Start Over With Spring Boot**
If you're serious about learning enterprise Java, start with Spring Boot from day one. It handles all the DI,
configuration, and database boilerplate for you.

### My Recommendation

**Complete the architecture.** You're 60% there. The patterns you attempted are correct for an enterprise application.
Don't abandon them - **finish the implementation**.

The refactoring steps I provided above will transform this from a broken mess into a clean, maintainable, testable
JavaFX application following proper MVC/layered architecture.

It will take 2-3 weeks of focused work, but you'll learn:

- How to properly implement design patterns
- How dependency injection actually works
- How to separate concerns correctly
- How to make testable, maintainable code

**This is worth more than any grade - it's actual professional experience.**

Good luck. You've got the knowledge - now finish the execution.

