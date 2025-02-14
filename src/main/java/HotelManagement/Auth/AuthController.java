package HotelManagement.Auth;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.Auth.dto.*;
import HotelManagement.Auth.user.User;
import HotelManagement.Auth.user.UserService;
import HotelManagement.jwt.JwtBlacklistService;
import HotelManagement.jwt.JwtService;
import HotelManagement.jwt.TokenRefreshRequest;
import HotelManagement.Auth.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JwtBlacklistService jwtBlacklistService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> saveEmployee(@RequestBody UserDTO userDTO) {
        ApiResponse<User> response = new ApiResponse<>();
        try {
            User user = userService.saveUser(userDTO);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("user saved successfully");
            response.setEntity(user);
        } catch (RuntimeException e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("internal server error please try again later");

        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmployee(@RequestBody Verification verification) {
        System.out.println("Received payload: " + verification);
        Long id = verification.getId();
        String verificationCode = verification.getVerificationCode();
        ApiResponse<String> response = new ApiResponse<>();

        // Check if the employee exists
        Optional<User> optionalEmployee = userRepository.findByIdAndDeletedFlag(id, "N");
        if (optionalEmployee.isEmpty()) {
            response.setMessage("Employee not found");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = optionalEmployee.get();

        // Check if the verification code is valid
        if (verificationCode == null || !verificationCode.equals(user.getVerificationCode())) {
            response.setMessage("Invalid verification code");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        LocalDateTime verificationTime = user.getVerificationTime();
        LocalDateTime expiryTime = verificationTime.plusMinutes(5);
        if (LocalDateTime.now().isAfter(expiryTime)) {
            response.setMessage("Verification code has expired");
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setVerifiedFlag("Y");
        userRepository.save(user);
        response.setMessage("User verified successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        ApiResponse response = new ApiResponse<>();

        try {
            User user = authService.forgotPassword(forgotPasswordDto);
            response.setMessage("Check your email for the verification code.");
            response.setStatusCode(HttpStatus.OK.value());
        } catch (RuntimeException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            response.setMessage("internal server error");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verifyForgotPassword")
    public ResponseEntity<ApiResponse> verifyForgotPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        ApiResponse response = new ApiResponse<>();
        try {
            User user = authService.verifyForgotPassword(resetPasswordDto);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("password reset successfully");

        } catch (RuntimeException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            response.setMessage("internal server error");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




    @PostMapping("/login")
    public ResponseEntity<ApiResponse> signIn(@RequestBody LoginDto loginDto) {
        ApiResponse response = new ApiResponse<>();
        try{
            LoginResponseDto loginResponse = authService.verify(loginDto);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("logged in  successfully");
            response.setEntity(loginResponse);

    } catch (RuntimeException e) {
        response.setMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
    } catch (Exception e) {
        response.setMessage("internal server error");
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
        return new ResponseEntity<>(response, HttpStatus.OK);
}



    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request) {
        ApiResponse response = new ApiResponse<>();
        String token = extractTokenFromRequest(request);
        response.setMessage("Logout Successful");
        response.setStatusCode(HttpStatus.OK.value());
        jwtBlacklistService.addToBlacklist(token); // Add token to blacklist
        return (response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate the refresh token
        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        String username = jwtService.extractUserName(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Convert GrantedAuthority to String
                .collect(Collectors.toList());

        System.out.println("validating.................");
        // Generate new access and refresh tokens
        String newAccessToken = jwtService.generateToken(username, authorities);
        String newRefreshToken = jwtService.generateRefreshToken(username, authorities);

        // Return tokens to the client
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return ResponseEntity.ok(tokens);
    }

}
