package HotelManagement.employee;

import HotelManagement.roles.Erole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private String verificationCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime verificationTime;

    private String verifiedFlag;
    private String lockedFlag;
    private String resetPasswordVerification;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime resetVerificationTime;

    private String deletedFlag;
    private List<Erole> roles;
    private String assignedTables;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime shiftStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime shiftEndTime;

    private List<EmployeeDTO> supervisedEmployees;
    private String shiftSchedule;
}
