package HotelManagement.apiIntergrationSecurity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "clients")
public class Client {
    @Id
    private UUID id;

    private String name;

    @Column(unique = true)
    private String appKey;

    private String appSecretHash;

    private boolean active;

    private LocalDateTime createdAt;


}
