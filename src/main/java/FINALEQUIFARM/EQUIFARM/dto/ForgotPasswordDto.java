package FINALEQUIFARM.EQUIFARM.dto;

import FINALEQUIFARM.EQUIFARM.model.Employee;
import lombok.Data;

@Data
public class ForgotPasswordDto {
    private String username;
    private String Email;
    private long phoneNumber;
    private String Password;
    private String confirmPassword;


    public ForgotPasswordDto(Employee employee, String username, String email, long phoneNumber, String password, String confirmPassword) {
        this.username = username;
        Email = email;
        this.phoneNumber = phoneNumber;
        Password = password;
        this.confirmPassword = confirmPassword;
    }
}

