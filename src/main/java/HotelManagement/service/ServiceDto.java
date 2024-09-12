package HotelManagement.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private String serviceName;
    private String description;
    private Double price;
    private ServiceType serviceType;
}
