package application.lab3.Service;

import application.lab3.entities.AppRole;
import application.lab3.entities.AppUser;

public interface AccountService {
    AppUser addNewUser(String username, String password,  String confirmPassword, String email);
    AppRole addNewRole(String role);
    void addRoleToUser(String username, String role);
    void removeRoleFromUser(String username, String role);
    AppUser getUserByUsername(String username);
}
