# Lab-3: Patient management application

## Introduction

The goal of this lab is to create a Management Application, with MVC design Pattern. 

### Dependencies

- Spring Data JPA
- Spring MVC
- Spring Web
- Thymleaf
- Lombok
- Mysql Driver

## Project's files structure

````
C:.
│   .gitignore
│   pom.xml
│   README.md
│
├───.idea
├───.mvn
├───src
│   ├───main
│   │   ├───java
│   │   │   └───application
│   │   │       └───lab3
│   │   │           │   Lab3Application.java
│   │   │           ├───entities
│   │   │           │       Patient.java
│   │   │           ├───repositories
│   │   │           │       PatientRepository.java
│   │   │           └───web
│   │   │                   PatientController.java
│   │   └───resources
│   │       │   application.properties
│   │       ├───static
│   │       └───templates
│   │               patients.html
│   └───test
└───target
````

## Code

### Entities

````java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate birthDate;
    private boolean ill;
    private int score;
}
````

### Repositories

````java
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
Page<Patient> findByNameContains (String keyword, Pageable pageable);
}
````

### Controllers

````java
@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;

    @GetMapping(path = "/index")
    public String patients(
            Model model,
            @RequestParam(name = "p", defaultValue = "0") int page,
            @RequestParam(name = "s", defaultValue = "4") int size,
            @RequestParam(name = "keyword", defaultValue = "") String keyword
    ) {
        Page<Patient> patients = patientRepository.findByNameContains(keyword, PageRequest.of(page, size));
        model.addAttribute("patientsList", patients.getContent());
        model.addAttribute("pagesNumber", new int[patients.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }

    @GetMapping("/delete")
    public String deletePatient(Long id, @RequestParam(name = "p") int page, String keyword) {
        patientRepository.deleteById(id);
        return "redirect:/index?p=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
}
````

