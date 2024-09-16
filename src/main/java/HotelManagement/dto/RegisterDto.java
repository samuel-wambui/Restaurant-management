package HotelManagement.dto;

import HotelManagement.roles.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegisterDto {

    private String username;
    private String password;
    private String phoneNumber;
    private String email;


    public RegisterDto(String username, String password, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;

    }
}
