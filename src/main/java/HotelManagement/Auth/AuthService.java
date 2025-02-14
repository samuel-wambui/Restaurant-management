package HotelManagement.Auth;

import HotelManagement.Auth.dto.ForgotPasswordDto;
import HotelManagement.Auth.dto.LoginResponseDto;
import HotelManagement.Auth.user.User;
import HotelManagement.Auth.user.UserRepository;
import HotelManagement.Auth.user.UserService;
import HotelManagement.EmailApp.EmailSender;
import HotelManagement.Auth.dto.LoginDto;
import HotelManagement.Auth.dto.ResetPasswordDto;
import HotelManagement.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private EmailSender emailSender;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 5;


    public LoginResponseDto signIn(LoginDto loginDto) {
        String username = loginDto.getEmail();
        int attempts = loginAttempts.getOrDefault(username, 0);

        // Check for max login attempts
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            throw new RuntimeException("Account locked due to many wrong logging attempts");
        }

        User user = userRepository.findByEmailAndDeletedFlag(username, "N")
                .orElseThrow(() -> new RuntimeException("Please register with us first."));

        if ("Y".equals(user.getDeletedFlag())) {
            throw new RuntimeException("Your account is deactivated, please contact support");
        }
        if ("Y".equals(user.getLockedFlag())) {
            throw new RuntimeException("Account locked. Please proceed to forgot password to reset your password");
        }
        if (!"Y".equals(user.getVerifiedFlag())) {
            throw new RuntimeException("Please finish registration by verifying your email");
        }

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDto.getPassword())
            );
            // Reset login attempts on success
            loginAttempts.put(username, 0);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate token and build the login response
            return verify(loginDto);
        } catch (BadCredentialsException ex) {
            loginAttempts.put(username, attempts + 1);
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts.get(username);

            // Lock account if necessary
            if (remainingAttempts <= 0) {
                user.setLockedFlag("Y");
                userRepository.save(user);
                throw new RuntimeException("Account locked due to many wrong logging attempts");
            }
            throw new RuntimeException("Incorrect username or password. " + remainingAttempts + " attempts remaining.");
        }
    }

    public LoginResponseDto verify(LoginDto loginDto) {
        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());

        // Convert authorities to a List<String>
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Generate the access token (refresh token generation is omitted here as it's not part of the response DTO)
        String accessToken = jwtService.generateToken(userDetails.getUsername(), roles);

        // Retrieve the user
        User user = userRepository.findByEmailAndDeletedFlag(loginDto.getEmail(), "N")
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build and return the login response DTO
        LoginResponseDto response = new LoginResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRoles(roles);
        response.setVerified("Y".equals(user.getVerifiedFlag()));
        response.setLocked("Y".equals(user.getLockedFlag()));
        response.setToken(accessToken);

        return response;
    }



    public User verifyForgotPassword(ResetPasswordDto resetPasswordDto) {
        User user = userRepository.findByEmailAndDeletedFlag(resetPasswordDto.getEmail(), "N")
                .orElseThrow(() -> new RuntimeException("User not found using email " + resetPasswordDto.getEmail()));

        if (!resetPasswordDto.getVerificationCode().equals(user.getResetPasswordVerification())) {
            throw new RuntimeException("Invalid verification code");
        }

        LocalDateTime expiryTime = user.getResetVerificationTime().plusMinutes(10);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            throw new RuntimeException("Verification code expired");
        }

        // Update password and unlock the account
        user.setLockedFlag("N");
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        loginAttempts.put(resetPasswordDto.getEmail(), 0);
        userRepository.save(user);

        // Send an email to notify the user
        String toEmail = user.getEmail();
        String text = "Dear " + user.getUsername() + ", your account has been unlocked.";
        emailSender.sendEmailWithVerificationCode(toEmail, "Account Unlocked", text);

        return user; // Ensure the method returns the updated user
    }

    public User forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository.findByEmailAndDeletedFlag(forgotPasswordDto.getEmail(), "N")
                .orElseThrow(() -> new RuntimeException("User not found using email " + forgotPasswordDto.getEmail()));

        user.setResetPasswordVerification(userService.generateCode(6));
        user.setResetVerificationTime(LocalDateTime.now());

        String toEmail = user.getEmail();
        String subject = "Password reset verification";
        String text = "Dear " + user.getUsername() + ", your reset password verification code is " + user.getResetPasswordVerification();

        emailSender.sendEmailWithVerificationCode(toEmail, subject, text);

        return userRepository.save(user);
    }
}
