package HotelManagement.employee;

import HotelManagement.repository.EmployeeRepository;
import HotelManagement.roles.Erole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeDTO saveEmployee(Employee employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        employee.generateVerificationCode();  // Generate code in entity
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    public Optional<EmployeeDTO> findById(Long id) {
        return employeeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeDTO> findAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesBySupervisor(Long supervisorId) {
        Employee supervisor = employeeRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        if (supervisor.getRoles().contains(Erole.ROLE_SUPERVISOR)) {
            return supervisor.getSupervisedEmployees().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("The supervisor role is required to retrieve supervised employees");
        }
    }

    public void assignSupervisedEmployee(Long supervisorId, Long employeeId) {
        Employee supervisor = employeeRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (supervisor.getRoles().contains(Erole.ROLE_SUPERVISOR)) {
            supervisor.addSupervisedEmployee(employee);
            employeeRepository.save(supervisor);
        } else {
            throw new RuntimeException("The supervisor role is required to assign employees");
        }
    }

    public void assignShiftToSupervisor(Long id, String shiftSchedule) {
        Employee supervisor = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));

        if (supervisor.getRoles().contains(Erole.ROLE_SUPERVISOR)) {
            supervisor.assignShiftSchedule(shiftSchedule);
            employeeRepository.save(supervisor);
        } else {
            throw new RuntimeException("The supervisor role is required to assign shift schedules");
        }
    }

    private Employee convertToEntity(Employee dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setUsername(dto.getUsername());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setEmail(dto.getEmail());
        employee.setPassword(dto.getPassword()); // This should be handled securely in real scenarios
        employee.setVerificationCode(dto.getVerificationCode());
        employee.setVerificationTime(dto.getVerificationTime());
        employee.setVerifiedFlag(dto.getVerifiedFlag());
        employee.setLockedFlag(dto.getLockedFlag());
        employee.setResetPasswordVerification(dto.getResetPasswordVerification());
        employee.setResetVerificationTime(dto.getResetVerificationTime());
        employee.setDeletedFlag(dto.getDeletedFlag());
        employee.setRoles(dto.getRoles());
        employee.setAssignedTables(dto.getAssignedTables());
        employee.setShiftStartTime(dto.getShiftStartTime());
        employee.setShiftEndTime(dto.getShiftEndTime());
        employee.setSupervisedEmployees(dto.getSupervisedEmployees().stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList()));
        employee.setShiftSchedule(dto.getShiftSchedule());
        return employee;
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setUsername(employee.getUsername());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setEmail(employee.getEmail());
        dto.setPassword(employee.getPassword());
        dto.setVerificationCode(employee.getVerificationCode());
        dto.setVerificationTime(employee.getVerificationTime());
        dto.setVerifiedFlag(employee.getVerifiedFlag());
        dto.setLockedFlag(employee.getLockedFlag());
        dto.setResetPasswordVerification(employee.getResetPasswordVerification());
        dto.setResetVerificationTime(employee.getResetVerificationTime());
        dto.setDeletedFlag(employee.getDeletedFlag());
        dto.setRoles(employee.getRoles());
        dto.setAssignedTables(employee.getAssignedTables());
        dto.setShiftStartTime(employee.getShiftStartTime());
        dto.setShiftEndTime(employee.getShiftEndTime());
        dto.setSupervisedEmployees(employee.getSupervisedEmployees().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        dto.setShiftSchedule(employee.getShiftSchedule());
        return dto;
    }

    public boolean verifyCode(Long id, String verificationCode) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return employee.verifyCode(verificationCode);
    }
}
