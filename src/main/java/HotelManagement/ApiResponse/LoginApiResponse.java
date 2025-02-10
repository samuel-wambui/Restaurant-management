package HotelManagement.ApiResponse;

import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginApiResponse <T> {
    private String message;
    private Integer statusCode;
    private EmployeeResponseDto employee;
    private T token;
    private T refreshToken;
}
