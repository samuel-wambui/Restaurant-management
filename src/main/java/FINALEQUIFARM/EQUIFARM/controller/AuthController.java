package FINALEQUIFARM.EQUIFARM.controller;

import FINALEQUIFARM.EQUIFARM.EmailApp.EmailSender;
import FINALEQUIFARM.EQUIFARM.EmailApp.Model;
import FINALEQUIFARM.EQUIFARM.dto.LoginDto;
import FINALEQUIFARM.EQUIFARM.dto.RegisterDto;
import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.repository.EmployeeRepository;
import FINALEQUIFARM.EQUIFARM.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private Model model;

    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final int MAX_LOGIN_ATTEMPTS = 3;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (employeeRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }
        if (employeeRepository.existsByPfNumber(registerDto.getPfNumber())) {
            return new ResponseEntity<>("PF Number is already registered", HttpStatus.BAD_REQUEST);
        }
        if (employeeRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already registered", HttpStatus.BAD_REQUEST);
        }

        Employee employee = new Employee();
        employee.setUsername(registerDto.getUsername());
        employee.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        employee.setPfNumber(registerDto.getPfNumber());
        employee.setEmail(registerDto.getEmail());

        employeeRepository.save(employee);


        // Send email with verification code
        String toEmail = employee.getEmail();
        String text = "Hello "+employee.getUsername() +" your verification code is "+employee.getVerificationCode() + ". Thank you!";
        emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);


        return ResponseEntity.ok("Employee registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String username = loginDto.getUsername();
        try {
            int attempts = loginAttempts.getOrDefault(username, 0);
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                throw new LockedException("Your account has been blocked due to too many failed login attempts.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            loginAttempts.put(username, 0);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok("Login successful!");
        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(ex.getMessage());
        } catch (Exception ex) {
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);

            int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;

            if (remainingAttempts <= 0) {
                loginAttempts.put(username, MAX_LOGIN_ATTEMPTS); // Reset attempts to max
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body("Your account has been blocked due to too many failed login attempts.");
            }

            return ResponseEntity.ok("Incorrect username or password. " + remainingAttempts + " attempts remaining.");
        }
    }
}
