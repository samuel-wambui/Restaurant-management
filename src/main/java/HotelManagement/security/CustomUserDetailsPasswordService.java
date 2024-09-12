package HotelManagement.security;


import HotelManagement.employee.Employee;
import HotelManagement.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsPasswordService implements UserDetailsPasswordService {

    // Assuming you have an EmployeeRepository or similar repository to update passwords
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsPasswordService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        // Update the password in your data source (e.g., database)
        Employee employee = employeeRepository.findByUsernameAndDeletedFlag(user.getUsername(),"N")
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        employee.setPassword(newPassword);  // Set the new password (should be encoded)
        employeeRepository.save(employee);  // Save updated employee to the database

        return user;  // Return the updated UserDetails object
    }
}
