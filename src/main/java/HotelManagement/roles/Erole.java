package HotelManagement.roles;

import HotelManagement.employee.Permissions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static HotelManagement.employee.Permissions.*;

@RequiredArgsConstructor
public enum Erole {
    USER(Collections.emptySet()),
    ROLE_SUPERUSER(Set.of(
            SUPERUSER_READ,
            SUPERUSER_CREATE,
            SUPERUSER_UPDATE,
            SUPERUSER_DELETE,
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_CREATE,
            ADMIN_DELETE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_CREATE,
            MANAGER_DELETE

    )),
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_CREATE,
            ADMIN_DELETE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_CREATE,
            MANAGER_DELETE
    )),
    MANAGER(Set.of(
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_CREATE,
            MANAGER_DELETE

    )),
    ROLE_SUPERVISOR(Collections.emptySet()),
    ROLE_WAITER(Collections.emptySet());

    @Getter
    private final Set<Permissions> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
