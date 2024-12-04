package HotelManagement.employee;

import HotelManagement.roles.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class EmployeeRoleDTO {
    private Long roleIds;
    private Long employeeId;
}
