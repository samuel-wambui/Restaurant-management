package HotelManagement.employee;

import HotelManagement.roles.Erole;
import HotelManagement.roles.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class Employee implements UserDetails {

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
 @JoinTable(
         name = "employee_roles",
         joinColumns = @JoinColumn(name = "employee_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
 )
 private List<Role> role = new ArrayList<>();


 // Username generation based on firstName, middleName, and lastName
 public String generateUserName() {

  if (middleName != null && !middleName.isEmpty()) {
   String initial = middleName.substring(0, 1).toUpperCase() + ". ";
   return firstName + " " + initial + lastName;
  }

  else if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
   return firstName + " " + lastName;
  }

  else if (firstName != null && !firstName.isEmpty()) {
   return firstName;
  } else if (lastName != null && !lastName.isEmpty()) {
   return lastName;
  }
  return "Anonymous User";
 }


 @PrePersist
 public void prePersist() {
  // Automatically generate and set username if not provided
  if (username == null || username.isEmpty()) {
   this.username = generateUserName();
  }
 }

 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
  if (role == null || role.isEmpty()) {
   return List.of();
  }
  return role.stream()
          .flatMap(r -> r.getAuthorities().stream())
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

 // Constructor for partial initialization
 public Employee(String username, String phoneNumber, String email, String password) {
  this.username = username;
  this.phoneNumber = phoneNumber;
  this.email = email;
  this.password = password;
  this.verifiedFlag = "N";
  this.deletedFlag = "N";
  this.lockedFlag = "N";
  generateVerificationCode();
  generateResetPasswordVerificationCode();
 }

 private String generateCode(int length) {
  final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  Random random = new Random();
  StringBuilder codeBuilder = new StringBuilder(length);

  for (int i = 0; i < length; i++) {
   int randomIndex = random.nextInt(CODE_CHARACTERS.length());
   codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
  }

  return codeBuilder.toString();
 }

 // Generate and set the verification code and time
 public String generateVerificationCode() {
  this.verificationCode = generateCode(6);
  this.verificationTime = LocalDateTime.now();
  return this.verificationCode; // Return the generated code
 }

 public boolean verifyCode(String code) {
  if (verificationCode.equals(code)) {
   LocalDateTime currentTime = LocalDateTime.now();
   LocalDateTime expiryTime = verificationTime.plusMinutes(5);
   return currentTime.isBefore(expiryTime);
  }
  return false;
 }

 public String generateResetPasswordVerificationCode() {
  this.resetPasswordVerification = generateCode(6);
  this.resetVerificationTime = LocalDateTime.now();
  return this.resetPasswordVerification;
 }

 public boolean validateResetPasswordCode(String code) {
  if (resetPasswordVerification.equals(code)) {
   LocalDateTime currentTime = LocalDateTime.now();
   LocalDateTime expiryTime = resetVerificationTime.plusMinutes(5);
   return currentTime.isBefore(expiryTime);
  }
  return false;
 }

 public void setVerifiedFlag(boolean isVerified) {
  this.verifiedFlag = isVerified ? "Y" : "N";
 }

 public void setLockedFlag(boolean isLocked) {
  this.lockedFlag = isLocked ? "Y" : "N";
 }

 public void setDeletedFlag(boolean isDeleted) {
  this.deletedFlag = isDeleted ? "Y" : "N";
 }


}
