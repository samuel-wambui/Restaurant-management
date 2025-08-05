package HotelManagement.apiIntergrationSecurity;

import HotelManagement.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthControllerClient {

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestParam String appKey,
                                      @RequestParam String appSecret) {

        Optional<ClientAppRegister> optionalClient = clientRepo.findByAppKey(appKey);
        if (optionalClient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        ClientAppRegister clientAppRegister = optionalClient.get();
        boolean match = new BCryptPasswordEncoder().matches(appSecret, clientAppRegister.getAppSecretHash());

        if (!match || !clientAppRegister.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String clientType = clientAppRegister.getClientType().toString(); // e.g., "PARTNER" or "SYSTEM"

        // Define scopes based on your rules, or leave empty if not used
        List<String> scopes = List.of("read", "write"); // Example scopes; adjust as needed

        String jwt = jwtService.generateClientToken(clientAppRegister.getAppKey(), clientType, scopes);

        return ResponseEntity.ok(Map.of("access_token", jwt));
    }


}