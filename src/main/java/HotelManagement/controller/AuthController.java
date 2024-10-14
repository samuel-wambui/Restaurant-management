package HotelManagement.controller;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.dto.ForgotPasswordDto;
import HotelManagement.dto.LoginDto;
import HotelManagement.dto.RegisterDto;
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
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterDto registerDto) {

        if (employeeRepository.existsByPhoneNumberAndDeletedFlag(registerDto.getPhoneNumber(), "N")) {
            return new ResponseEntity<>(Map.of("message", "Phone number is already registered"), HttpStatus.BAD_REQUEST);
        }
        if (employeeRepository.existsByEmailAndDeletedFlag(registerDto.getEmail(), "N")) {
            return new ResponseEntity<>(Map.of("message", "Email is already registered"), HttpStatus.BAD_REQUEST);
        }


        Employee employee = new Employee();
        employee.setUsername(registerDto.getFirstName());
        employee.setUsername(registerDto.getMiddleName());
        employee.setUsername(registerDto.getLastName());
        employee.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        employee.setPhoneNumber(registerDto.getPhoneNumber());
        employee.setEmail(registerDto.getEmail());

        employee.generateVerificationCode();
        employeeRepository.save(employee);

        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getUsername() + ", your verification code is " + employee.getVerificationCode() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, "Email Verification", text);
        //Collection<? extends GrantedAuthority> authorities = employee.getAuthorities();

        //String token = jwtService.generateToken(employee.getUsername(), Collections.singletonList(authorities.toString()));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee registered successfully.");
        //response.put("token", token);

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
    public ResponseEntity<String> signIn(@RequestBody LoginDto loginDto) {
        return employeeService.signIn(loginDto);
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
