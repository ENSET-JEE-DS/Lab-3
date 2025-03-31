package application.lab3.Service;

import application.lab3.entities.AppRole;
import application.lab3.entities.AppUser;
import application.lab3.repositories.AppRoleRepository;
import application.lab3.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//        appUserRepository.save(appUser);

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
