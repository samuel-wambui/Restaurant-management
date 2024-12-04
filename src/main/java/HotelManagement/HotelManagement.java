package HotelManagement;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.controller.AuthController;
import HotelManagement.dto.LoginDto;
import HotelManagement.dto.RegisterDto;
import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeService;
import HotelManagement.repository.EmployeeRepository;
import HotelManagement.roles.Erole;
import HotelManagement.roles.Role;
import HotelManagement.roles.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class HotelManagement {
	public static void main(String[] args) {
		SpringApplication.run(HotelManagement.class, args);
	}


//	@Bean
//	CommandLineRunner initSuperUser(EmployeeRepository userRepository, PasswordEncoder passwordEncoder) {
//		return args -> {
//			String superUsername = "admin";
//			String superPassword = "admin123";
//
//			userRepository.findByUsername(superUsername).ifPresentOrElse(
//					user -> System.out.println("Super user already exists."),
//					() -> {
//						Employee superUser = new Employee();
//						superUser.setUsername(superUsername);
//						superUser.setPassword(passwordEncoder.encode(superPassword));
////						superUser.setRole(Erole.ROLE_SUPERUSER);
//
//						userRepository.save(superUser);
//						System.out.println("Super user created.");
//					}
//			);
//		};
//	}

}
