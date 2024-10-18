package HotelManagement.controller;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.dto.ForgotPasswordDto;
import HotelManagement.dto.LoginDto;
import HotelManagement.dto.RegisterDto;
import HotelManagement.dto.ResetPasswordDto;
import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeService;
import HotelManagement.jwt.JwtService;
import HotelManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
    public ResponseEntity<ApiResponse<Map<String, String>>> register(@RequestBody RegisterDto registerDto) {


        if (employeeRepository.existsByPhoneNumberAndDeletedFlag(registerDto.getPhoneNumber(), "N")) {
            ApiResponse<Map<String, String>> response = new ApiResponse<>();
            response.setMessage("Phone number is already registered");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (employeeRepository.existsByEmailAndDeletedFlag(registerDto.getEmail(), "N")) {
            ApiResponse<Map<String, String>> response = new ApiResponse<>();
            response.setMessage("Email is already registered");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Create new Employee object and set its properties
        Employee employee = new Employee();
        employee.setFirstName(registerDto.getFirstName());
        employee.setMiddleName(registerDto.getMiddleName());
        employee.setLastName(registerDto.getLastName());
        employee.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        employee.setPhoneNumber(registerDto.getPhoneNumber());
        employee.setEmail(registerDto.getEmail());
        employee.generateVerificationCode();
        employeeRepository.save(employee);
        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getFirstName() + ", your verification code is " + employee.getVerificationCode() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, "Email Verification", text);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Employee registered successfully.");
        apiResponse.setEntity(employee);
        apiResponse.setStatusCode(HttpStatus.CREATED.value());


        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmployee(@RequestBody Verification verification) {
        System.out.println("Received payload: " + verification);
        Long id = verification.getId();
        String verificationCode = verification.getVerificationCode();
        ApiResponse<String> response = new ApiResponse<>();

        // Check if the employee exists
        Optional<Employee> optionalEmployee = employeeRepository.findByIdAndDeletedFlag(id, "N");
        if (optionalEmployee.isEmpty()) {
            response.setMessage("Employee not found");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Employee employee = optionalEmployee.get();

        // Check if the verification code is valid
        if (verificationCode == null || !verificationCode.equals(employee.getVerificationCode())) {
            response.setMessage("Invalid verification code");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            System.out.println("Received payload: " + verification);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        LocalDateTime verificationTime = employee.getVerificationTime();
        LocalDateTime expiryTime = verificationTime.plusMinutes(5);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            employeeService.deleteEmployee(id);
            response.setMessage("Verification code has expired");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        employee.setVerifiedFlag(true);
        employeeService.saveEmployee(employee);
        String toEmail = employee.getEmail();
        String text = "Dear " + employee.getUsername() + ", your account has been successfully verified.";
        emailSender.sendEmailWithVerificationCode(toEmail, "Account Verified", text);
        response.setMessage("User verified successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> signIn(@RequestBody LoginDto loginDto) {

        ResponseEntity<ApiResponse> serviceResponse = employeeService.signIn(loginDto);


        return serviceResponse;
    }



    @PostMapping("/forgotPassword")
    public ApiResponse forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmailAndDeletedFlag(forgotPasswordDto.getEmail() , "N");
        ApiResponse response = new ApiResponse<>();

        if (optionalEmployee.isEmpty()) {
            response.setMessage("Employee not found or verification failed.");
            response.setStatusCode(HttpStatus.NOT_FOUND.ordinal());
            return(response);

        }

        Employee employee = optionalEmployee.get();
        employee.generateResetPasswordVerificationCode();

        employeeRepository.save(employee);

        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getUsername() + ", your reset password verification code is " + employee.getResetPasswordVerification() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);
        response.setMessage("Check your email for the verification code.");
        response.setStatusCode(HttpStatus.FOUND.ordinal());

        return(response);
    }

    @PostMapping("/verifyForgotPassword")
    public ApiResponse verifyForgotPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return employeeService.verifyForgotPassword(resetPasswordDto);
    }}
