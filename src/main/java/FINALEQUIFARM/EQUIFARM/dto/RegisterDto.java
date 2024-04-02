package FINALEQUIFARM.EQUIFARM.dto;



import FINALEQUIFARM.EQUIFARM.model.Employee;
import lombok.Data;

@Data
public class RegisterDto {
    private String verificationCode;
    private String username;
    private String password;
    private int pfNumber;
    private String email;


    public RegisterDto(String username, String password, int pfNumber, String email,String verificationCode) {
        this.username = username;
        this.password = password;
        this.pfNumber = pfNumber;
        this.email = email;
        this.verificationCode = verificationCode;


    }
}
