package HotelManagement.employee;

import HotelManagement.roles.Erole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class Employee {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private long id;

 @Column(name = "username")
 private String username;

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

 @ElementCollection(targetClass = Erole.class)
 @Enumerated(EnumType.STRING)
 @CollectionTable(name = "employee_roles", joinColumns = @JoinColumn(name = "employee_id"))
 @Column(name = "role")
 private List<Erole> roles;

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
