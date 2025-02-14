package HotelManagement.Auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private List<String> roles;
    private boolean verified;
    private boolean locked;
    private String token;
}
