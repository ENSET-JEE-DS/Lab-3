package application.lab3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
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
