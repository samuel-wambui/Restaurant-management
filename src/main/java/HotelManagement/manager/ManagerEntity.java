package HotelManagement.manager;

import HotelManagement.roles.Erole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Managers")
public class ManagerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Erole role = Erole.ROLE_MANAGER;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    // Additional fields if necessary
}
