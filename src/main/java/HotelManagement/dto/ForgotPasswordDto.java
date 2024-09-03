package HotelManagement.dto;

import HotelManagement.model.Employee;
import lombok.Data;

@Data
public class ForgotPasswordDto {
    private String username;
    private String Email;
    private String phoneNumber;
    private String Password;
    private String confirmPassword;


    public ForgotPasswordDto(Employee employee, String username, String email, String phoneNumber, String password, String confirmPassword) {
        this.username = username;
        Email = email;
        this.phoneNumber = phoneNumber;
        Password = password;
        this.confirmPassword = confirmPassword;
    }
}

