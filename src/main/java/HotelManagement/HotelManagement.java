package HotelManagement;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.controller.AuthController;
import HotelManagement.dto.LoginDto;
import HotelManagement.dto.RegisterDto;
import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeService;
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
	public CommandLineRunner commandLineRunner(EmployeeService employeeService, RoleRepository roleRepository) {
		return args -> {
			String adminUsername = "sam";
			String adminEmail = "hadricawamalwa@gmail.com";
			String adminPassword = "sam@123";
			Role adminRole = roleRepository.findByName("ADMIN")
					.orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
			if (employeeRepository.existsByUsername(adminUsername)) {
				authenticateUser(employeeService, adminUsername, adminPassword);
			} else {
				registerUser(employeeService, emailSender, adminUsername, adminEmail, adminPassword, "1234567891", adminRole);
			}

			String managerUsername = "Waiter";
			String managerEmail = "manager@example.commm";
			String managerPassword = "manager@123";
			Role managerRole = roleRepository.findByName("MANAGER")
					.orElseThrow(() -> new RuntimeException("Error: Role MANAGER is not found."));
			if (employeeRepository.existsByUsername(managerUsername)) {
				authenticateUser(employeeService, managerUsername, managerPassword);
			} else {
				registerUser(employeeService, emailSender, managerUsername, managerEmail, managerPassword, "19876543212", managerRole);
			}
		};
	}

	private void authenticateUser(EmployeeService employeeService, String username, String password) {

		LoginDto loginDto = new LoginDto();
		loginDto.setUsername(username);
		loginDto.setPassword(password);
		ResponseEntity<String> response = employeeService.signIn(loginDto);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
		} else {
			System.out.println(username + " authentication failed: ");
		}
	}

	private void registerUser(EmployeeService employeeService, EmailSender emailSender, String username, String email, String password, String phoneNumber, Role role) {
		Employee newEmployee= Employee.builder()
				.username(username)
				.email(email)
				.password(password)  // Ensure hashing happens in the controller
				.phoneNumber(phoneNumber)
				.role(List.of(role))
				.verifiedFlag("Y")
				.build();

		ResponseEntity<Map<String, String>> response = (ResponseEntity<Map<String, String>>) employeeService.registration(newEmployee);

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

		} else {
			System.out.println("Registration failed: " + response.getBody().get("message"));
		}
	}

}
