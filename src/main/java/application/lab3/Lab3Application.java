package application.lab3;

import application.lab3.entities.Patient;
import application.lab3.repositories.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }


    @Bean
    CommandLineRunner run(PatientRepository patientRepository) {
        return args -> {
            System.out.println("Application Started");

            patientRepository.save(new Patient(null, "Paul", LocalDate.now(), true, 90));

        };
    }
}
