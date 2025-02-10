package HotelManagement.employee;

import lombok.Data;

import java.util.List;
@Data
public class EmployeeResponseDto  {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private List<String> roles;
    private List<String> permissions;
    private boolean verified;
    private boolean locked;


}
