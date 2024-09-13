package HotelManagement;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.controller.AuthController;
import HotelManagement.employee.Employee;
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

	@Bean
	public CommandLineRunner commandLineRunner(AuthController authController, RoleRepository roleRepository) {
		return args -> {
			// Create Admin
			Role adminRole = roleRepository.findByName("ADMIN")
					.orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
			Employee adminRequest = Employee.builder()
					.username("Admin")
					.email("samuelngari13@gmail.com")
					.password("sam@123")
					.phoneNumber("1234567890")
					.role(List.of(adminRole)) // Using List.of() to ensure the list is immutable
					.verificationCode(new Employee().generateVerificationCode())
					.verificationTime(LocalDateTime.now())
					.build();
			ResponseEntity<Map<String, String>> adminResponse = authController.register(adminRequest);
			String adminToken = adminResponse.getBody().get("token");
			System.out.println("Admin auth token: " + adminToken);
			emailSender.sendEmailWithVerificationCode(adminRequest.getEmail(),
					"Verification code",
					"Dear " + adminRequest.getUsername() + ", your verification code is " + adminRequest.getVerificationCode());

			// Create Manager
			Role managerRole = roleRepository.findByName("MANAGER")
					.orElseThrow(() -> new RuntimeException("Error: Role MANAGER is not found."));
			Employee managerRequest = Employee.builder()
					.username("Manager")
					.email("manager@example.com")
					.password("manager@123")
					.phoneNumber("0987654321")
					.role(List.of(managerRole)) // Using List.of() to ensure the list is immutable
					.verificationCode(new Employee().generateVerificationCode())
					.verificationTime(LocalDateTime.now())
					.build();
			ResponseEntity<Map<String, String>> managerResponse = authController.register(managerRequest);
			String managerToken = managerResponse.getBody().get("token");
			System.out.println("Manager auth token: " + managerToken);
			emailSender.sendEmailWithVerificationCode(managerRequest.getEmail(),
					"Verification code",
					"Dear " + managerRequest.getUsername() + ", your verification code is " + managerRequest.getVerificationCode());
		};
	}
}
