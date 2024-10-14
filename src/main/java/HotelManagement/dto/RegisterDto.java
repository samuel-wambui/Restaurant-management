package HotelManagement.dto;

import HotelManagement.roles.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegisterDto {

    private String firstName;
    private String middleName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private String email;


    public RegisterDto(String firstName,String middleName,String lastName, String password, String phoneNumber, String email) {
        this.firstName= firstName;
        this.middleName= middleName;
        this.lastName= lastName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;

    }
}
