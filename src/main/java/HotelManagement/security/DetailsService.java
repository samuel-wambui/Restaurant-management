package HotelManagement.security;

import HotelManagement.Auth.user.User;
import HotelManagement.Auth.user.UserRepository;
import HotelManagement.roles.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public DetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Adjusted to check for "N" (not deleted) in the deletedFlag
        User user = userRepository.findByEmailAndDeletedFlag(email, "N")
                .orElseThrow(() -> new UsernameNotFoundException("email not found" ));

        // Mapping roles to authorities (permissions)
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRole()));
    }

    // Adjusted to work with the Erole enum for authorities
    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getAuthorities().stream())
                .collect(Collectors.toList());
    }
}
