package HotelManagement.controller;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.dto.ForgotPasswordDto;
import HotelManagement.dto.LoginDto;
import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeService;
import HotelManagement.jwt.JwtService;
import HotelManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private Model model;
    @Autowired
    JwtService jwtService;

    @Autowired
    private EmployeeService employeeService;

    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final int MAX_LOGIN_ATTEMPTS = 3;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Employee registerDto) {
        // Check if the username already exists and is not marked as deleted
        if (employeeRepository.existsByUsernameAndDeletedFlag(registerDto.getUsername(), "N")) {
            return new ResponseEntity<>(Map.of("message", "Username is already taken"), HttpStatus.BAD_REQUEST);
        }

        // Check if the phone number already exists and is not marked as deleted
        if (employeeRepository.existsByPhoneNumberAndDeletedFlag(registerDto.getPhoneNumber(), "N")) {
            return new ResponseEntity<>(Map.of("message", "Phone number is already registered"), HttpStatus.BAD_REQUEST);
        }

        // Check if the email already exists and is not marked as deleted
        if (employeeRepository.existsByEmailAndDeletedFlag(registerDto.getEmail(), "N")) {
            return new ResponseEntity<>(Map.of("message", "Email is already registered"), HttpStatus.BAD_REQUEST);
        }

        // Create a new employee and set the details
        Employee employee = new Employee();
        employee.setUsername(registerDto.getUsername());
        employee.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        employee.setPhoneNumber(registerDto.getPhoneNumber());
        employee.setEmail(registerDto.getEmail());
        employee.setRole(registerDto.getRole());

        // Generate verification code and set verification time
        employee.generateVerificationCode();

        // Save the new employee in the database
        employeeRepository.save(employee);

        // Send verification email
        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getUsername() + ", your verification code is " + employee.getVerificationCode() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, "Email Verification", text);
        Collection<? extends GrantedAuthority> authorities = employee.getAuthorities();// Get the employee's authorities (make sure it's a List<String>)

// Generate the token by passing both username and authorities
        String token = jwtService.generateToken(employee.getUsername(), Collections.singletonList(authorities.toString()));

        // Return the success message along with the generated token
        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee registered successfully.");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmployee(@RequestParam Long id, @RequestParam String verificationCode) {

        Optional<Employee> optionalEmployee = employeeRepository.findByIdAndDeletedFlag(id, "N");

        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.badRequest().body("Employee not found");

        }

        Employee employee = optionalEmployee.get();
        if (!verificationCode.equals(employee.getVerificationCode())) {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }

        LocalDateTime verificationTime = employee.getVerificationTime();
        LocalDateTime expiryTime = verificationTime.plusMinutes(5);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            employeeService.deleteEmployee(id);
            return ResponseEntity.badRequest().body("Verification code has expired");
        }

        employee.setVerifiedFlag(true);
        employeeService.saveEmployee(employee);

        String toEmail = employee.getEmail();
        String text = "Dear " + employee.getUsername() + ", your account has been successfully verified.";
        emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

        return ResponseEntity.ok("User verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String username = loginDto.getUsername();
        int attempts = loginAttempts.getOrDefault(username, 0);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            return ResponseEntity.status(HttpStatus.LOCKED).body("Account locked due to too many failed login attempts.");
        }

        Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag(loginDto.getUsername(),"N");
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please register first.");
        }

        Employee employee = optionalEmployee.get();
        if ("Y".equals(employee.getDeletedFlag())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account deactivated or deleted. Contact support.");
        }

        if ("Y".equals(employee.getLockedFlag())) {
            return ResponseEntity.status(HttpStatus.LOCKED).body("Your account is locked. Please reset your password.");
        }

        if (!"Y".equals(employee.getVerifiedFlag())) {
            return ResponseEntity.status(HttpStatus.OK).body("Complete registration first.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDto.getPassword())
            );

            loginAttempts.put(username, 0);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return employeeService.verify(loginDto);  // Assuming this returns a ResponseEntity
        } catch (BadCredentialsException ex) {
            loginAttempts.put(username, attempts + 1);
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts.get(username);

            if (remainingAttempts <= 0) {

                employee.setLockedFlag(true);
                employeeRepository.save(employee);
                return ResponseEntity.status(HttpStatus.LOCKED).body("Account locked due to too many failed login attempts.");

            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password. " + remainingAttempts + " attempts remaining.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login.");
        }
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag("N", forgotPasswordDto.getUsername());


        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found or verification failed.");

        }

        Employee employee = optionalEmployee.get();
        employee.generateResetPasswordVerificationCode();
        employee.setPassword(passwordEncoder.encode(forgotPasswordDto.getConfirmPassword()));
        employeeRepository.save(employee);

        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getUsername() + ", your reset password verification code is " + employee.getResetPasswordVerification() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

        return ResponseEntity.ok("Check your email for the verification code.");
    }

    @PostMapping("/verifyForgotPassword")
    public ResponseEntity<String> verifyForgotPassword(@RequestParam String username, @RequestParam String verificationCode) {
        Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag("N", username);


        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.badRequest().body("Employee not found.");

        }

        Employee employee = optionalEmployee.get();
        if (!verificationCode.equals(employee.getResetPasswordVerification())) {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }

        LocalDateTime expiryTime = employee.getResetVerificationTime().plusMinutes(3);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            return ResponseEntity.badRequest().body("Verification code has expired.");
        }

        employee.setLockedFlag(false);
        employeeRepository.save(employee);

        String toEmail = employee.getEmail();
        String text = "Dear " + employee.getUsername() + ", your account has been unlocked.";
        emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

        return ResponseEntity.ok("Password updated successfully.");
    }
}
