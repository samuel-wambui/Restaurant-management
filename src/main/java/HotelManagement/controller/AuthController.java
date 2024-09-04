package HotelManagement.controller;


import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.dto.ForgotPasswordDto;
import HotelManagement.dto.LoginDto;
import HotelManagement.dto.RegisterDto;
import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeService;
import HotelManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private EmployeeService employeeService;

    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final int MAX_LOGIN_ATTEMPTS = 3;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (employeeRepository.existsByUsernameAndDeletedFlag("N", registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        if (employeeRepository.existsByEmailAndDeletedFlag("N", String.valueOf(registerDto.getPhoneNumber()))) {
            return new ResponseEntity<>("PF Number is already registered", HttpStatus.BAD_REQUEST);
        }
        if (employeeRepository.existsByPhoneNumberAndDeletedFlag("N", registerDto.getPhoneNumber())) {
            return new ResponseEntity<>("Email is already registered", HttpStatus.BAD_REQUEST);
        }

        Employee employee = new Employee();
        employee.setUsername(registerDto.getUsername());
        employee.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        employee.setPhoneNumber(registerDto.getPhoneNumber());
        employee.setEmail(registerDto.getEmail());

        // Generate verification code and set verification time
        employee.generateVerificationCode();

        employeeRepository.save(employee);

        // Send email with verification code
        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getUsername() + ", your verification code is " + employee.getVerificationCode() + ". Thank you!";
        emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

        return ResponseEntity.ok("Employee registered successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmployee(@RequestParam Long id, @RequestParam String verificationCode) {
        try {
            // Retrieve the employee by ID
            Optional<Employee> optionalEmployee = employeeRepository.findByIdAndDeletedFlag(id, "N");


            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();

                // Check if the verification code matches
                if (verificationCode.equals(employee.getVerificationCode())) {
                    // Check if the verification code is expired
                    LocalDateTime verificationTime = employee.getVerificationTime();
                    LocalDateTime currentTime = LocalDateTime.now();
                    LocalDateTime expiryTime = verificationTime.plusMinutes(5); // Expiry time is 5 minutes after generation

                    if (currentTime.isAfter(expiryTime)) {
                        employeeService.deleteEmployee(id);
                        return ResponseEntity.badRequest().body("Verification code has expired");
                    }

                    // Update the employee's verified flag to "Y"
                    employee.setVerifiedFlag(true);
                    employeeService.saveEmployee(employee);

                    // Send email to confirm verification
                    String toEmail = employee.getEmail();
                    String text = "Dear " + employee.getUsername() + ", your account has been successfully verified. Thank you!";
                    emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

                    return ResponseEntity.ok("User verified successfully");
                } else {
                    return ResponseEntity.badRequest().body("Verification code does not match");
                }
            } else {
                return ResponseEntity.badRequest().body("Employee not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to verify employee: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String username = loginDto.getUsername();
        try {
            int attempts = loginAttempts.getOrDefault(username, 0);
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                throw new LockedException("Your account has been blocked due to too many failed login attempts. Please reset your password.");
            }

            Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag("N", username);

            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                if (employee.getDeletedFlag().equals("Y")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account deactivated or deleted. Please contact support.");
                } else if (employee.getLockedFlag().equals("Y")) {
                    return ResponseEntity.status(HttpStatus.LOCKED).body("Your account has been locked. Please reset your password.");
                } else if (!employee.getVerifiedFlag().equals("Y")) {
                    return ResponseEntity.status(HttpStatus.OK).body("Finish registration process first.");
                } else {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
                    );

                    loginAttempts.put(username, 0);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    return ResponseEntity.ok("Login successful!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please register first.");
            }
        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(ex.getMessage());
        } catch (Exception ex) {
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;

            if (remainingAttempts <= 0) {
                Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag("N", username);
                if (optionalEmployee.isPresent()) {

                    Employee employee = optionalEmployee.get();
                    if (employee.getLockedFlag().equals("N")) {
                        employee.setLockedFlag(true);
                        employeeRepository.save(employee);
                        return ResponseEntity.status(HttpStatus.LOCKED).body("Your account has been locked. Please reset your password.");
                    }
                }

                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body("Your account has been blocked due to too many failed login attempts. Please click forgot password.");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect username or password. " + remainingAttempts + " attempts remaining.");
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        // Check if the employee exists and verify their identity
        if (employeeRepository.existsByUsernameAndDeletedFlag("N", forgotPasswordDto.getUsername())
                && employeeRepository.existsByPhoneNumberAndDeletedFlag("N", forgotPasswordDto.getPhoneNumber())
                && employeeRepository.existsByEmailAndDeletedFlag("N", forgotPasswordDto.getEmail())) {

            // Find the employee by username
            Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag("N", forgotPasswordDto.getUsername());

            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();

                // Use the verification code received in the request
                employee.generateResetPasswordVerificationCode();

                // Update the password (assuming you want to update the password, not confirmation password)
                employee.setPassword(passwordEncoder.encode(forgotPasswordDto.getConfirmPassword()));

                // Save the updated employee
                employeeRepository.save(employee);

                // Send email with verification code
                String toEmail = employee.getEmail();
                String text = "Hello " + employee.getUsername() + ", your reset password verification code is " + employee.getResetPasswordVerification()+ ". Thank you!";
                emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

                // Return success response
                return ResponseEntity.ok("Check your email for verification code");
            }
        }

        // If employee not found or identity verification fails, return appropriate error response
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found or identity verification failed.");
    }

    @PostMapping("/verifyForgotPassword")
    public ResponseEntity<String> verifyForgotPassword(@RequestParam String username, @RequestParam String verificationCode) {
        try {
            // Retrieve the employee by username
            Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndDeletedFlag("N", username);

            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();

                // Check if the verification code matches
                if (verificationCode.equals(employee.getResetPasswordVerification())) {
                    // Check if the verification code is expired
                    LocalDateTime verificationTime = employee.getResetVerificationTime();
                    LocalDateTime currentTime = LocalDateTime.now();
                    LocalDateTime expiryTime = verificationTime.plusMinutes(3); // Expiry time is 5 minutes after generation

                    if (currentTime.isAfter(expiryTime)) {
                        return ResponseEntity.badRequest().body("Verification code has expired");
                    }

                    // Update the employee's locked flag to "N"
                    employee.setLockedFlag(false);
                    employeeRepository.save(employee);

                    // Send email to confirm verification
                    String toEmail = employee.getEmail();
                    String text = "Dear " + employee.getUsername() + ", your account has been successfully unlocked. Thank you!";
                    emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

                    return ResponseEntity.ok("User password updated successfully");
                } else {
                    return ResponseEntity.badRequest().body("Verification code does not match");
                }
            } else {
                return ResponseEntity.badRequest().body("Employee not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to verify employee: " + e.getMessage());
        }
    }
}
