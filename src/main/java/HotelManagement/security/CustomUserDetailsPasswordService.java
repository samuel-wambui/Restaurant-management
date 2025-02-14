package HotelManagement.security;


import HotelManagement.Auth.user.User;
import HotelManagement.Auth.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsPasswordService implements UserDetailsPasswordService {

    // Assuming you have an EmployeeRepository or similar repository to update passwords
    private final UserRepository userRepository;

    public CustomUserDetailsPasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        // Update the password in your data source (e.g., database)
        User employee = userRepository.findByUsernameAndDeletedFlag(user.getUsername(),"N")
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        employee.setPassword(newPassword);  // Set the new password (should be encoded)
        userRepository.save(employee);  // Save updated employee to the database

        return user;  // Return the updated UserDetails object
    }
}
