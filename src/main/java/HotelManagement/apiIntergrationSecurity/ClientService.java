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

    public ClientDTO registerNewClient(String name, ClientType clientType) {
        String appKey = UUID.randomUUID().toString();
        String appSecret = UUID.randomUUID().toString();
        String hashedSecret = new BCryptPasswordEncoder().encode(appSecret);

        ClientAppRegister clientAppRegister = new ClientAppRegister();
        clientAppRegister.setId(UUID.randomUUID());
        clientAppRegister.setName(name);
        clientAppRegister.setAppKey(appKey);
        clientAppRegister.setAppSecretHash(hashedSecret);
        clientAppRegister.setClientType(clientType); // Set the client type
        clientAppRegister.setActive(true);
        clientAppRegister.setCreatedAt(LocalDateTime.now());

        clientRepo.save(clientAppRegister);

        return new ClientDTO(appKey, appSecret); // return the plain secret only once
    }

}
