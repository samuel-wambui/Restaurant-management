package HotelManagement.dto;

import HotelManagement.roles.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDto {

    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private Role role;

    public RegisterDto(String username, String password, String phoneNumber, String email,Role role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role=role;
    }
}
