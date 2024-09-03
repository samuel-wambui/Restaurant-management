package FINALEQUIFARM.EQUIFARM.dto;


import lombok.Data;

@Data
public class RegisterDto {
    private String verificationCode;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private long id;


    public RegisterDto(String verificationCode, String username, String password, String phoneNumber, String email, long id) {
        this.verificationCode = verificationCode;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.id = id;
    }
}

