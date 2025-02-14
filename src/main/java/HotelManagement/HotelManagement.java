package HotelManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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
