package application.lab3;

import application.lab3.entities.Patient;
import application.lab3.repositories.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }


    @Bean
    CommandLineRunner run(PatientRepository patientRepository) {
        return args -> {
            System.out.println("Application Started");

//            for (int i = 0; i < 4; i++) {
//            patientRepository.save(new Patient(null, "Paul Mccartney", LocalDate.now(), true, 90));
//            patientRepository.save(new Patient(null, "John Lennon", LocalDate.now(), false, 12));
//            patientRepository.save(new Patient(null, "Ringo Starr", LocalDate.now(), true, 20));
//            patientRepository.save(new Patient(null, "George Harrison", LocalDate.now(), true, 58));
//            patientRepository.save(new Patient(null, "George Martin", LocalDate.now(), true, 19));
//            }

        };
    }

    @Bean
    CommandLineRunner runUsersManager(JdbcUserDetailsManager jdbcUserDetailsManager) {
        PasswordEncoder passwordEncoder = passwordEncoder();
        return args -> {
            if (!jdbcUserDetailsManager.userExists("user")) {
                jdbcUserDetailsManager.createUser(User
                        .withUsername("user")
                        .password(passwordEncoder.encode("user"))
                        .roles("USER").build());
            }

            if (!jdbcUserDetailsManager.userExists("admin")) {
                jdbcUserDetailsManager.createUser(User
                        .withUsername("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles("ADMIN", "USER").build());
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
