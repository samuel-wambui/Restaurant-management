package HotelManagement.manager;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class ManagerDto {
    private String firstname;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
}
