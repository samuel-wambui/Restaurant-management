package HotelManagement.employee;


import HotelManagement.EmailApp.EmailSender;
import HotelManagement.controller.LoginResponseDto;
import HotelManagement.dto.LoginDto;
import HotelManagement.jwt.JwtService;
import HotelManagement.repository.EmployeeRepository;
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

    public Employee updateEmployee(Employee employee) {
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

    public ResponseEntity<LoginResponseDto> signIn(LoginDto loginDto) {
        String username = loginDto.getEmail();
        int attempts = loginAttempts.getOrDefault(username, 0);

        // Check for max login attempts
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new LoginResponseDto("Account locked due to too many failed login attempts.", null));
        }

        Optional<Employee> optionalEmployee = employeeRepository.findByEmailAndDeletedFlag(username, "N");
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDto("Please register first.", null));
        }

        Employee employee = optionalEmployee.get();

        if ("Y".equals(employee.getDeletedFlag())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDto("Account deactivated or deleted. Contact support.", null));
        }

        if ("Y".equals(employee.getLockedFlag())) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new LoginResponseDto("Your account is locked. Please reset your password.", null));
        }

        if (!"Y".equals(employee.getVerifiedFlag())) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto("Complete registration first.", null));
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
            return verify(loginDto);
        } catch (BadCredentialsException ex) {
            loginAttempts.put(username, attempts + 1);
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts.get(username);

            // Lock account if necessary
            if (remainingAttempts <= 0) {
                employee.setLockedFlag(true);
                employeeRepository.save(employee);
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(new LoginResponseDto("Account locked due to too many failed login attempts.", null));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDto("Incorrect username or password. " + remainingAttempts + " attempts remaining.", null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDto("An error occurred during login.", null));
        }
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

    public ResponseEntity<LoginResponseDto> verify(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            // Load the user by username
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());

            // Convert the authorities to a List<String>
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority()) // Convert GrantedAuthority to String
                    .collect(Collectors.toList());

            // Generate the token with username and authorities
            String token = jwtService.generateToken(userDetails.getUsername(), authorities);

            System.out.println("JWT Token: " + token);

            // Return the success response with the token and message
            LoginResponseDto response = new LoginResponseDto("Login successful", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDto("fail", null));
        }
    }

}
