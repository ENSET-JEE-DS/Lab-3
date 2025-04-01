# Lab-3: Spring MVC, Spring Data JPA Thymeleaf, Spring Security

## Overview

This project is a simple Spring Boot application for managing patients. It provides functionalities to add, update, delete, and search for patients. The application uses Thymeleaf for rendering HTML views and integrates with a MySQL database for data persistence.

## Goal
The goal of this project is to demonstrate the use of Spring Boot, Spring Data JPA, Thymeleaf, and MySQL in building a web application. It covers basic CRUD operations and pagination.

## Dependencies

The project uses the following dependencies:

- **Spring Boot Starter**: Core starter for Spring Boot applications.
- **Spring Boot Starter Web**: Starter for building web applications using Spring MVC.
- **Spring Boot Starter Data JPA**: Starter for using Spring Data JPA with Hibernate.
- **Spring Boot Starter Thymeleaf**: Starter for using Thymeleaf as the view layer.
- **Spring Boot Starter Validation**: Starter for using Java Bean Validation with Hibernate Validator.
- **Spring Boot Starter Security**: Starter for using Spring Security.
- **Lombok**: Library for reducing boilerplate code by using annotations.
- **MySQL Connector**: JDBC driver for MySQL.
- **Bootstrap**: Front-end framework for responsive web design.
- **Thymeleaf Layout Dialect**: Extension for Thymeleaf to support layout and template inheritance.

## Major Code Parts

### Entities

#### `Patient` Entity

The `Patient` class represents the patient entity with fields. It uses JPA annotations for ORM mapping and validation annotations for input validation.

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 20)
    private String name;

    private LocalDate birthDate;
    private boolean ill;

    @DecimalMax("100")
    private int score;
}
```

#### `AppUser` Entity

The `AppUser` class represents the user entity with fields for ID, username, password, email, and a list of roles. It uses JPA annotations for ORM mapping and Lombok annotations for boilerplate code reduction.

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    @Column(unique=true)
    private String username;
    private String password;
    @Column(unique=true)
    private String email;
    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    private List<AppRole> roleList = new ArrayList<>();
}
```

#### `AppRole` Entity

The `AppRole` class represents the role entity with a single field for the role name. It uses JPA annotations for ORM mapping and Lombok annotations for boilerplate code reduction.

```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRole {
    @Id
    private String roleName;
}
```

### Repositories
#### `PatientRepository`

The `PatientRepository` interface extends `JpaRepository` to provide CRUD operations and a custom query method to find patients by name containing a keyword.

```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByNameContains(String keyword, Pageable pageable);
}
```

#### `AppRoleRepository`

The `AppRoleRepository` interface extends `JpaRepository` to provide CRUD operations for the `AppRole` entity.

```java
@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByRoleName(String roleName);
}
```

#### `AppUserRepository`

The `AppUserRepository` interface extends `JpaRepository` to provide CRUD operations for the `AppUser` entity.

```java
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
```

### Services

#### `AccountService`

The `AccountService` interface defines methods for managing users and roles.

```java
public interface AccountService {
    AppUser addNewUser(String username, String password, String confirmPassword, String email);
    AppRole addNewRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
    AppUser getUserByUsername(String username);
}
```

#### `AccountServiceImpl`

The `AccountServiceImpl` class implements the `AccountService` interface and provides the logic for managing users and roles.

```java
@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser addNewUser(String username, String password, String confirmPassword, String email) {
        if (appUserRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (!password.equals(confirmPassword)) throw new RuntimeException("Passwords do not match");

        AppUser appUser = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .build();

        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole addNewRole(String role) {
        if (appRoleRepository.findByRoleName(role) != null) throw new RuntimeException("Role already exists");
        AppRole appRole = AppRole.builder()
                .roleName(role)
                .build();

        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) throw new RuntimeException("User not found");

        AppRole appRole = appRoleRepository.findByRoleName(role);
        if (appRole == null) throw new RuntimeException("Role not found");

        appUser.getRoleList().add(appRole);
    }

    @Override
    public void removeRoleFromUser(String username, String role) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) throw new RuntimeException("User not found");

        AppRole appRole = appRoleRepository.findByRoleName(role);
        if (appRole == null) throw new RuntimeException("Role not found");

        appUser.getRoleList().remove(appRole);
    }

    @Override
    public AppUser getUserByUsername(String username) {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) throw new RuntimeException("User not found");
        return appUser;
    }
}
```

#### `UserDetailsServiceImpl`

The `UserDetailsServiceImpl` class implements the `UserDetailsService` interface and provides the logic for loading user details by username.

```java
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = accountService.getUserByUsername(username);
        if (appUser == null) throw new UsernameNotFoundException("Username " + username + " not found");

        List<String> userRoleList = appUser.getRoleList().stream().map(AppRole::getRoleName).toList();
        String[] roles = appUser.getRoleList().stream().map(AppRole::getRoleName).toArray(String[]::new);

        return User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(roles)
                .build();
    }
}
```

### Controllers
#### `PatientController`

The `PatientController` class handles HTTP requests for managing patients. It includes methods for listing, adding, updating, and deleting patients, as well as handling pagination and search functionality.

```java
@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping(path = "/index")
    public String patients(Model model, @RequestParam(name = "p", defaultValue = "0") int page, @RequestParam(name = "s", defaultValue = "4") int size, @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Patient> patients = patientRepository.findByNameContains(keyword, PageRequest.of(page, size));
        model.addAttribute("patientsList", patients.getContent());
        model.addAttribute("pagesNumber", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @GetMapping("/delete")
    public String deletePatient(Long id, @RequestParam(name = "p") int page, @RequestParam(required = false) String keyword) {
        patientRepository.deleteById(id);
        if (keyword != null) {
            return "redirect:/index?p=" + page + "&keyword=" + keyword;
        }
        return "redirect:/index?p=" + page;
    }

    @GetMapping("/addPatient")
    public String addPatient(Model model) {
        model.addAttribute("patient", new Patient());
        return "patientForm";
    }

    @PostMapping("/addPatient")
    public String addPatient(@ModelAttribute @Valid Patient patient, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "patientForm";
        }
        patientRepository.save(patient);
        return "redirect:/index";
    }

    @GetMapping("/update")
    public String updatePatient(Long id, Model model) {
        Patient patient = patientRepository.findById(id).get();
        model.addAttribute("patient", patient);
        return "patientFormUpdate";
    }
}
```

#### `SecurityController`

The `SecurityController` class handles HTTP requests for login and authorization error pages.

```java
@Controller
public class SecurityController {

    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
```

### Configurations

### `SecurityConfig`

The `SecurityConfig` class configures Spring Security for the application. It sets up JDBC/inMemory/UserDetailsService user details management, form login, authorization rules, and exception handling.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

//    JDBC  Authentication
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

//    InMemory Authentication
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user").password(passwordEncoder.encode("user")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder.encode("user")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build()
        );
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/delete/**").hasRole("ADMIN")
                        .requestMatchers("/update/**").hasRole("ADMIN")
                        .requestMatchers("/addPatient/**").hasRole("ADMIN")
                        .requestMatchers("/index").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                
                .exceptionHandling(exception ->
                        exception.accessDeniedPage("/notAuthorized"))
                
                .rememberMe(remember -> remember
                        .key("remember")
                        .rememberMeCookieName("remember-cookie")
                        .rememberMeParameter("remember-me"))
                // UserDetailsService Authentication
                .userDetailsService(userDetailsServiceImpl)
                
               .build();
    }

}
```

### Thymeleaf Templates

The project uses Thymeleaf templates for rendering HTML views. The main templates are:

- `patients.html`: Displays the list of patients with search and pagination functionality.
- `patientForm.html`: Form for adding a new patient.
- `patientFormUpdate.html`: Form for updating an existing patient.
- `login.html`: Login page for user authentication.
- `notAuthorized.html`: Page displayed when a user is not authorized to access a resource.
- `template-1.html`: Base layout template for the application.

## Configuration

The application is configured to connect to a MySQL database. The database connection properties are specified in the `application.properties` file.

```ini
spring.datasource.url=jdbc:mysql://localhost:3306/patients_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yahya
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
spring.thymeleaf.cache=false
spring.devtools.livereload.enabled=true
```

## Running the Application

To run the application, use the following command:

```bash
mvn spring-boot:run
```

The application will start and be accessible at `http://localhost:8080`.

## Conclusion

This project demonstrates a basic Spring Boot application with CRUD operations, pagination, search functionality, and security features using Thymeleaf and MySQL. It serves as a good starting point for building more complex web applications with Spring Boot.

