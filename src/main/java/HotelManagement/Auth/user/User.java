package HotelManagement.Auth.user;

import HotelManagement.Departments.DepartmentEntity;
import HotelManagement.roles.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private long id;

 @Column(name = "userName")
 private String username;

 @Column(name = "first_name")
 private String firstName;

 @Column(name = "middle_name")
 private String middleName;

 @Column(name = "last_name")
 private String lastName;

 @Column(name = "phone_number")
 private String phoneNumber;

 @Column(name = "email")
 private String email;

 @Column(name = "password")
 private String password;

 @Column(name = "verification_code")
 private String verificationCode;

 @Column(name = "verification_time")
 private LocalDateTime verificationTime;

 @Column(name = "verified_flag")
 private String verifiedFlag = "N";

 @Column(name = "locked_flag")
 private String lockedFlag = "N";

 @Column(name = "reset_password_verification")
 private String resetPasswordVerification;

 @Column(name = "reset_verification_time")
 private LocalDateTime resetVerificationTime;

 @Column(name = "deleted_flag")
 private String deletedFlag = "N";

 @ManyToMany(fetch = FetchType.EAGER)
 @JsonIgnore
 @JoinTable(
         name = "user_roles",
         joinColumns = @JoinColumn(name = "user_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
 )
 private List<Role> role = new ArrayList<>();

 @ManyToMany
 @JoinTable(
         name = "user_department",
         joinColumns = @JoinColumn(name = "user_id"),
         inverseJoinColumns = @JoinColumn(name = "department_id")
 )
 private Set<DepartmentEntity> departments = new HashSet<>();
 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
  if (role == null || role.isEmpty()) {
   return List.of();
  }
  return role.stream()
          .map(r -> new SimpleGrantedAuthority(r.getName()))  // Return only the role name
          .collect(Collectors.toList());
 }



 @Override
 public String getPassword() {
  return password;
 }

 @Override
 public String getUsername() {
  return username;
 }

 @Override
 public boolean isAccountNonExpired() {
  return true;
 }

 @Override
 public boolean isAccountNonLocked() {
  return !lockedFlag.equals("Y");
 }

 @Override
 public boolean isCredentialsNonExpired() {
  return true;
 }

 @Override
 public boolean isEnabled() {
  return !deletedFlag.equals("Y");
 }

 }




