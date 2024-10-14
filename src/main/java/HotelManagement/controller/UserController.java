/*package FINALEQUIFARM.EQUIFARM.controller;

import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.model.Role;
import FINALEQUIFARM.EQUIFARM.repository.EmployeeRepository;
import FINALEQUIFARM.EQUIFARM.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/{userId}/assign-roles")
    public ResponseEntity<?> assignRolesToUser(@PathVariable("userId") Long userId,
                                                  @RequestBody List<Role> roles,
                                                  Authentication authentication) {
        // Get the authenticated user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Load the user making the request from the database
        boolean requestingEmployee = employeeRepository.findByUsernameAndIsDeletedFalse(userDetails.getUsername());

        // Check if the requesting user has permission to assign roles
        if (!requestingEmployee) {
            requestingEmployee.isAdmin();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to assign roles.");

        // Load the user to whom roles will be assigned
    }
}
*/
//	@Autowired
//	EmailSender emailSender;
//
//	@Autowired
//	EmployeeRepository employeeRepository;
//
//	@Bean
//	public CommandLineRunner commandLineRunner(EmployeeService employeeService, RoleRepository roleRepository) {
//		return args -> {
//			String adminUsername = "sam";
//			String adminEmail = "hadricawamalwa@gmail.com";
//			String adminPassword = "sam@123";
//			Role adminRole = roleRepository.findByName("ADMIN")
//					.orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
//			if (employeeRepository.existsByUsername(adminUsername)) {
//				authenticateUser(employeeService, adminUsername, adminPassword);
//			} else {
//				registerUser(employeeService, emailSender, adminUsername, adminEmail, adminPassword, "1234567891", adminRole);
//			}
//
//			String managerUsername = "Waiter";
//			String managerEmail = "manager@example.commm";
//			String managerPassword = "manager@123";
//			Role managerRole = roleRepository.findByName("MANAGER")
//					.orElseThrow(() -> new RuntimeException("Error: Role MANAGER is not found."));
//			if (employeeRepository.existsByUsername(managerUsername)) {
//				authenticateUser(employeeService, managerUsername, managerPassword);
//			} else {
//				registerUser(employeeService, emailSender, managerUsername, managerEmail, managerPassword, "19876543212", managerRole);
//			}
//		};
//	}
//
//	private void authenticateUser(EmployeeService employeeService, String username, String password) {
//
//		LoginDto loginDto = new LoginDto();
//		loginDto.setUsername(username);
//		loginDto.setPassword(password);
//		ResponseEntity<String> response = employeeService.signIn(loginDto);
//        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//			String token = response.getBody().substring(7);
//			System.out.println(username + " auth token: " + token);
//		} else {
//			System.out.println(username + " authentication failed: ");
//		}
//	}
//
//	private void registerUser(EmployeeService employeeService, EmailSender emailSender, String username, String email, String password, String phoneNumber, Role role) {
//		Employee newEmployee= Employee.builder()
//				.username(username)
//				.email(email)
//				.password(password)  // Ensure hashing happens in the controller
//				.phoneNumber(phoneNumber)
//				.role(List.of(role))
//				.verifiedFlag("Y")
//				.build();
//
//		ResponseEntity<Map<String, String>> response = (ResponseEntity<Map<String, String>>) employeeService.registration(newEmployee);
//
//		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//			String token = response.getBody().get("token");
//			System.out.println(username + " auth token: " + token);
//		} else {
//			System.out.println("Registration failed: " + response.getBody().get("message"));
//		}
//	}
