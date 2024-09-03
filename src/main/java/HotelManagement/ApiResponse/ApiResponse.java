package HotelManagement.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private Integer statusCode;

    // Constructor with only message
    public ApiResponse(String message) {
        this.message = message;
    }
}
