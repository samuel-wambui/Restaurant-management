package HotelManagement.apiIntergrationSecurity.patnerDetails;

import lombok.Data;

@Data
public class PartnerRegisterDto {
    private String clientFirstName;
    private String clientLastName;
    private String email;
    private String password;
}
