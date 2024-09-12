package HotelManagement.service;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "service")
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String description;
    private Double price;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;  // Add this if you're using an enum
}
