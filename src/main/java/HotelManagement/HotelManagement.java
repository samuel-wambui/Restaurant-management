package HotelManagement;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.controller.AuthController;
import HotelManagement.dto.LoginDto;
import HotelManagement.employee.Employee;
import HotelManagement.repository.EmployeeRepository;
import HotelManagement.roles.Role;
import HotelManagement.roles.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class HotelManagement {
	public static void main(String[] args) {
		SpringApplication.run(HotelManagement.class, args);
	}

	@Autowired
	EmailSender emailSender;

	@Autowired
	EmployeeRepository employeeRepository;

	@Bean
	public CommandLineRunner commandLineRunner(AuthController authController, RoleRepository roleRepository) {
		return args -> {
			String adminUsername = "Admin";
			String adminEmail = "samuelngari13@gmail.com";
			String adminPassword = "sam@123";
			Role adminRole = roleRepository.findByName("ADMIN")
					.orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
			if (employeeRepository.existsByUsername(adminUsername)) {
				authenticateUser(authController, adminUsername, adminPassword);
			} else {
				registerUser(authController, emailSender, adminUsername, adminEmail, adminPassword, "1234567890", adminRole);
			}

			String managerUsername = "Manager";
			String managerEmail = "manager@example.com";
			String managerPassword = "manager@123";
			Role managerRole = roleRepository.findByName("MANAGER")
					.orElseThrow(() -> new RuntimeException("Error: Role MANAGER is not found."));
			if (employeeRepository.existsByUsername(managerUsername)) {
				authenticateUser(authController, managerUsername, managerPassword);
			} else {
				registerUser(authController, emailSender, managerUsername, managerEmail, managerPassword, "0987654321", managerRole);
			}
		};
	}

	private void authenticateUser(AuthController authController, String username, String password) {

		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(username);
		loginDto.setPassword(password);

		// Authenticate user using login
		ResponseEntity<String> response = authController.login(loginDto);
		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			String token = response.getBody().substring(7);
			System.out.println(username + " auth token: " + token);
		} else {
			System.out.println(username + " authentication failed: ");
		}
	}

	private void registerUser(AuthController authController, EmailSender emailSender, String username, String email, String password, String phoneNumber, Role role) {
		// Register new user
		Employee newUser = Employee.builder()
				.username(username)
				.email(email)
				.password(password) // Ensure this is hashed properly in the controller
				.phoneNumber(phoneNumber)
				.role(List.of(role))
				.verificationCode(new Employee().generateVerificationCode())
				.verificationTime(LocalDateTime.now())
				.build();
		ResponseEntity<Map<String, String>> response = authController.register(newUser);
		String token = response.getBody().get("token");
		System.out.println(username + " auth token: " + token);


		emailSender.sendEmailWithVerificationCode(newUser.getEmail(),
				"Verification code",
				"Dear " + newUser.getUsername() + ", your verification code is " + newUser.getVerificationCode());
	}
}
