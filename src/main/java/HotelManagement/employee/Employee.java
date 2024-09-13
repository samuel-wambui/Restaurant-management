package HotelManagement.employee;

import HotelManagement.roles.Erole;
import HotelManagement.roles.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Employee implements UserDetails {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private long id;

 @Column(name = "username", nullable = false)
 private String username;

 @Column(name = "phone_number", nullable = false)
 private String phoneNumber;

 @Column(name = "email", nullable = false)
 private String email;

 @Column(name = "password", nullable = false)
 private String password;

 @Column(name = "verification_code")
 private String verificationCode;

 @Column(name = "verification_time")
 private LocalDateTime verificationTime;

 @Column(name = "verified_flag", nullable = false)
 private String verifiedFlag = "N";

 @Column(name = "locked_flag", nullable = false)
 private String lockedFlag = "N";

 @Column(name = "reset_password_verification")
 private String resetPasswordVerification;

 @Column(name = "reset_verification_time")
 private LocalDateTime resetVerificationTime;

 @Column(name = "deleted_flag", nullable = false)
 private String deletedFlag = "N";

 @ManyToMany(fetch = FetchType.EAGER)
 @JoinTable(
         name = "employee_roles",
         joinColumns = @JoinColumn(name = "employee_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
 )
 private List<Role> role;

 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
  return role.stream()
          .flatMap(r -> r.getAuthorities().stream())
          .collect(Collectors.toList());
 }

 // Other UserDetails interface methods
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

 public void generateVerificationCode() {
  this.verificationCode = generateCode(6);
  this.verificationTime = LocalDateTime.now();
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
