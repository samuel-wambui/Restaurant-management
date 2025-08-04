package HotelManagement.apiIntergrationSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepo;

    public ClientDTO registerNewClient(String name) {
        String appKey = UUID.randomUUID().toString();
        String appSecret = UUID.randomUUID().toString();
        String hashedSecret = new BCryptPasswordEncoder().encode(appSecret);

        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setName(name);
        client.setAppKey(appKey);
        client.setAppSecretHash(hashedSecret);
        client.setActive(true);
        client.setCreatedAt(LocalDateTime.now());

        clientRepo.save(client);

        return new ClientDTO(appKey, appSecret);
    }
}

