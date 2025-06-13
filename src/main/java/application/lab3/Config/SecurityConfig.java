package application.lab3.Config;

import application.lab3.Service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private PasswordEncoder passwordEncoder;
    private UserDetailsServiceImpl userDetailsServiceImpl;


//    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user").password(passwordEncoder.encode("user")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder.encode("user")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build()
        );
    }

//    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
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

                .userDetailsService(userDetailsServiceImpl)

               .build();
    }

}