# Lab-3

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
- **Lombok**: Library for reducing boilerplate code by using annotations.
- **MySQL Connector**: JDBC driver for MySQL.
- **Bootstrap**: Front-end framework for responsive web design.
- **Thymeleaf Layout Dialect**: Extension for Thymeleaf to support layout and template inheritance.

## Major Code Parts

### `Patient` Entity

The `Patient` class represents the patient entity with fields for ID, name, birth date, illness status, and score. It uses JPA annotations for ORM mapping and validation annotations for input validation.

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

### `PatientRepository`

The `PatientRepository` interface extends `JpaRepository` to provide CRUD operations and a custom query method to find patients by name containing a keyword.

```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByNameContains(String keyword, Pageable pageable);
}
```

### `PatientController`

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

### Thymeleaf Templates

The project uses Thymeleaf templates for rendering HTML views. The main templates are:

- `patients.html`: Displays the list of patients with search and pagination functionality.
- `patientForm.html`: Form for adding a new patient.
- `patientFormUpdate.html`: Form for updating an existing patient.
- `template-1.html`: Base layout template for the application.

## Configuration

The application is configured to connect to a MySQL database. The database connection properties are specified in the `application.properties` file.

```ini
spring.datasource.url=jdbc:mysql://localhost:3306/patients_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yahya
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

## Running the Application

To run the application, use the following command:

```bash
mvn spring-boot:run
```

The application will start and be accessible at `http://localhost:8080`.

## Conclusion

This project demonstrates a basic Spring Boot application with CRUD operations, pagination, and search functionality using Thymeleaf and MySQL. It serves as a good starting point for building more complex web applications with Spring Boot.
