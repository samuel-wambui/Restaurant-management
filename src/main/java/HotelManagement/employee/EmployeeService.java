package HotelManagement.employee;


import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.ApiResponse.LoginApiResponse;
import HotelManagement.EmailApp.EmailSender;
import HotelManagement.controller.LoginResponseDto;
import HotelManagement.dto.LoginDto;
import HotelManagement.dto.ResetPasswordDto;
import HotelManagement.jwt.JwtService;
import HotelManagement.repository.EmployeeRepository;
import HotelManagement.roles.Role;
import HotelManagement.roles.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Autowired
    private EmailSender emailSender;
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 5;


    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(long id) {
        return employeeRepository.findById(id);
    }

//    public Optional<Employee> findByUsername(String username) {
//        return employeeRepository.findByUsername(username);
//    }

    public Employee assignRole(EmployeeRoleDTO employeeRoleDTO) {
        // Retrieve the employee from the database using the ID
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeRoleDTO.getEmployeeId());
        if (!optionalEmployee.isPresent()) {
            throw new RuntimeException("Employee not found with ID: " + employeeRoleDTO.getEmployeeId());
        }
        Employee employee = optionalEmployee.get();

        // Retrieve roles based on role IDs from the DTO
        List<Role> roles = roleRepository.findAllById(Collections.singleton(employeeRoleDTO.getRoleIds()));
        if (roles.isEmpty()) {
            throw new RuntimeException("No roles found for the provided role IDs");
        }

        // Assign roles to the employee
        employee.setRole(roles);

        // Save and return the updated employee
        return employeeRepository.save(employee);
    }


    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }

    public String generateVerificationCode(long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.generateVerificationCode();
            employeeRepository.save(emp);
            return emp.getVerificationCode();
        }
        return null;
    }

    public boolean verifyCode(long id, String code) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return emp.verifyCode(code);
        }
        return false;
    }

    public String generateResetPasswordVerificationCode(long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return emp.generateResetPasswordVerificationCode();
        }
        return null;
    }

    public boolean validateResetPasswordCode(long id, String code) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return emp.validateResetPasswordCode(code);
        }
        return false;
    }

    public void setEmployeeVerified(long id, boolean isVerified) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setVerifiedFlag(isVerified);
            employeeRepository.save(emp);
        }
    }

    public void setEmployeeLocked(long id, boolean isLocked) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setLockedFlag(isLocked);
            employeeRepository.save(emp);
        }
    }

    public void setEmployeeDeleted(long id, boolean isDeleted) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setDeletedFlag(isDeleted);
            employeeRepository.save(emp);
        }
    }

    private String generateCode(int length) {
        final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CODE_CHARACTERS.length());
            codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }



    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }




    public Map<String, String> registration(Employee registerDto) {
        if (employeeRepository.existsByUsernameAndDeletedFlag(registerDto.getUsername(), "N")) {
            return Map.of("message", "Username is already taken");
        }
        if (employeeRepository.existsByPhoneNumberAndDeletedFlag(registerDto.getPhoneNumber(), "N")) {
            return Map.of("message", "Phone number is already registered");
        }
        if (employeeRepository.existsByEmailAndDeletedFlag(registerDto.getEmail(), "N")) {
            return Map.of("message", "Email is already registered");
        }

        Employee employee = new Employee();
        employee.setUsername(registerDto.getUsername());
        employee.setPassword(passwordEncoder.encode(registerDto.getPassword()));  // Hash the password
        employee.setPhoneNumber(registerDto.getPhoneNumber());
        employee.setEmail(registerDto.getEmail());
        employee.setRole(registerDto.getRole());
        employee.generateVerificationCode();

        employeeRepository.save(employee);


        String toEmail = employee.getEmail();
        String text = "Hello " + employee.getUsername() + ", your verification code is " + employee.getVerificationCode() + ".";
        emailSender.sendEmailWithVerificationCode(toEmail, "Email Verification", text);
        Collection<? extends GrantedAuthority> authorities = employee.getAuthorities(); // List<String>
        String token = jwtService.generateToken(employee.getUsername(), Collections.singletonList(authorities.toString()));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee registered successfully.");
        response.put("token", token);

        return response;
    }
    public ResponseEntity<LoginApiResponse> signIn(LoginDto loginDto) {
        LoginApiResponse response = new LoginApiResponse();
        String username = loginDto.getEmail();
        int attempts = loginAttempts.getOrDefault(username, 0);

        // Check for max login attempts
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            response.setMessage("Account locked due to too many failed login attempts.");
            response.setStatusCode(HttpStatus.LOCKED.value());
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }

        Optional<Employee> optionalEmployee = employeeRepository.findByEmailAndDeletedFlag(username, "N");
        if (optionalEmployee.isEmpty()) {
            response.setMessage("Please register first.");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Employee employee = optionalEmployee.get();

        if ("Y".equals(employee.getDeletedFlag())) {
            response.setMessage("Account deactivated or deleted. Contact support.");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if ("Y".equals(employee.getLockedFlag())) {
            response.setMessage("Your account is locked. Please reset your password.");
            response.setStatusCode(HttpStatus.LOCKED.value());
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }

        if (!"Y".equals(employee.getVerifiedFlag())) {
            response.setMessage("Complete registration first.");
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        }

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDto.getPassword())
            );

            // Reset login attempts on success
            loginAttempts.put(username, 0);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Call the verify method to return the token and message
            return verify(loginDto); // Return the response from verify, which includes the token
        } catch (BadCredentialsException ex) {
            loginAttempts.put(username, attempts + 1);
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts.get(username);

            // Lock account if necessary
            if (remainingAttempts <= 0) {
                employee.setLockedFlag(true);
                employeeRepository.save(employee);
                response.setMessage("Account locked due to too many failed login attempts.");
                response.setStatusCode(HttpStatus.LOCKED.value());
                return ResponseEntity.status(HttpStatus.LOCKED).body(response);
            }

            response.setMessage("Incorrect username or password. " + remainingAttempts + " attempts remaining.");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception ex) {
            response.setMessage("An error occurred during login: " + ex.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<LoginApiResponse> verify(LoginDto loginDto) {
        LoginApiResponse response = new LoginApiResponse();

        try {
            // Load the user by username
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
            String userName = loginDto.getEmail();

            // Convert the authorities to a List<String>
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // Convert GrantedAuthority to String
                    .collect(Collectors.toList());

            // Generate the token with username and authorities
            String token = jwtService.generateToken(userName, authorities);
            String refreshToken = jwtService.generateRefreshToken(userName,authorities);

            System.out.println("JWT Token: " + token);
            System.out.println("refreshTocken" + refreshToken);

            // Set success response
            response.setMessage("Login successful");
            response.setToken(token);
            response.setRefreshToken(refreshToken);// Set the token in the response
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Handle exceptions and return an error response
            response.setMessage("An error occurred during login: " + ex.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    public ApiResponse verifyForgotPassword(ResetPasswordDto resetPasswordDto) {
        ApiResponse response = new ApiResponse<>();
        Optional<Employee> optionalEmployee = employeeRepository.findByEmailAndDeletedFlag(resetPasswordDto.getEmail(), "N");

        if (optionalEmployee.isEmpty()) {
            response.setMessage("Employee not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }

        if (resetPasswordDto.getVerificationCode() == null) {
            response.setMessage("Missing verification code");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            throw new IllegalArgumentException("Missing verification code");
        }

        Employee employee = optionalEmployee.get();
        if (!resetPasswordDto.getVerificationCode().equals(employee.getResetPasswordVerification())) {
            response.setMessage("Invalid verification code");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        LocalDateTime expiryTime = employee.getResetVerificationTime().plusMinutes(10);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            response.setMessage("Verification code expired");
            response.setStatusCode(HttpStatus.GONE.value());
            return response;
        }

        // Update the password and unlock the account
        employee.setLockedFlag(false);
        employee.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        loginAttempts.put(resetPasswordDto.getEmail(), 0);
        employeeRepository.save(employee);


        // Send an email to the user
        String toEmail = employee.getEmail();
        String text = "Dear " + employee.getUsername() + ", your account has been unlocked.";
        emailSender.sendEmailWithVerificationCode(toEmail, "Account Unlocked", text);

        response.setMessage("Password updated successfully.");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }
}
