package HotelManagement.dto;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String Email;
    private String Password;
    private  String verificationCode;

    public ResetPasswordDto(String email, String password,String verificationCode) {
       this.Email  = email;
       this.Password = password;
       this.verificationCode= verificationCode;

    }
}
