package HotelManagement.roles;

import HotelManagement.Auth.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @ElementCollection(targetClass = Permissions.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private Set<Permissions> permissions;

    // Operational Audit fields
    @Column(length = 30, nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String postedBy;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date postedTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String modifiedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date modifiedTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String verifiedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date verifiedTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String deletedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date deletedTime;

    @ManyToMany(mappedBy = "role")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<User> users = new HashSet<>();

    // Convert Role to Spring Security authorities
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name));
        return authorities;
    }
}
