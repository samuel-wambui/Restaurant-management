package HotelManagement.dto;


import lombok.Data;

@Data
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

