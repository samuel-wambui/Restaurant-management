package HotelManagement.apiIntergrationSecurity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "clients")
public class ClientAppRegister {
    @Id
    private UUID id;

    private String name;

    @Column(unique = true)
    private String appKey;

    private String appSecretHash;

    private boolean active;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

}
