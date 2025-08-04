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

        Optional<Client> optionalClient = clientRepo.findByAppKey(appKey);
        if (optionalClient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Client client = optionalClient.get();
        boolean match = new BCryptPasswordEncoder().matches(appSecret, client.getAppSecretHash());

        if (!match || !client.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        //String jwt = jwtService.generateToken(client.getAppKey()); // subject = appKey
        //return ResponseEntity.ok(Map.of("access_token", jwt));
        return null;
    }
}