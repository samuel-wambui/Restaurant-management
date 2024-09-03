package HotelManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
 private long phoneNumber;

 @Column(name = "email")
 private String email;

 @Column(name = "password")
 private String password;

 @Column(name = "verification_code")
 private String verificationCode;

 @Column(name = "verification_time")
 private LocalDateTime verificationTime;

 @Column(name = "verified")
 private boolean verified = false;

 @Column(name = "locked")
 private boolean locked = false;

 @Column(name = "reset_password_verification")
 private String resetPasswordVerification;

 @Column(name = "reset_verification_time")
 private LocalDateTime resetVerificationTime;

 @Setter
 @Column(name = "deleted")
 private boolean deleted = false;

 @ManyToMany
 @JoinTable(
         name = "employee_roles",
         joinColumns = @JoinColumn(name = "employee_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
 )
 private List<Role> roles;

 public Employee(String username, long phoneNumber, String email, String password) {
  this.username = username;
  this.phoneNumber = phoneNumber;
  this.email = email;
  this.password = password;
  this.verified = false;
  this.deleted = false;
  this.locked = false;
  generateVerificationCode();
  generateResetPasswordVerificationCode();
 }

 public void generateVerificationCode() {
  final int CODE_LENGTH = 6;
  final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  Random random = new Random();
  StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);

  for (int i = 0; i < CODE_LENGTH; i++) {
   int randomIndex = random.nextInt(CODE_CHARACTERS.length());
   codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
  }

  this.verificationCode = codeBuilder.toString();
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
  final int CODE_LENGTH = 6;
  final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  Random random = new Random();
  StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);

  for (int i = 0; i < CODE_LENGTH; i++) {
   int randomIndex = random.nextInt(CODE_CHARACTERS.length());
   codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
  }

  this.resetPasswordVerification = codeBuilder.toString();
  this.resetVerificationTime = LocalDateTime.now();
  return CODE_CHARACTERS;
 }

 public boolean resetPasswordVerificationCode(String code) {
  if (resetPasswordVerification.equals(code)) {
   LocalDateTime currentTime = LocalDateTime.now();
   LocalDateTime expiryTime = resetVerificationTime.plusMinutes(5);
   return currentTime.isBefore(expiryTime);
  }
  return false;
 }
}






/*package FINALEQUIFARM.EQUIFARM.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
 private int phoneNumber;

 @Column(name = "email")
 private String email;

 @Column(name = "password")
 private String password;

 @Column(name = "verification_code")
 private String verificationCode;

 @Column(name = "verification_time")
 private LocalDateTime verificationTime;

 @Column(name = "verified")
 private boolean verified = false;

 @Column(name = "locked")
 private boolean locked = false;

 @Column(name = "reset_password_verification")
 private String resetPasswordVerification;

 @Column(name = "reset_verification_time")
 private LocalDateTime resetVerificationTime;

 @Setter
 @Column(name = "deleted")
 private boolean deleted = false;

 @ManyToMany
 @JoinTable(
         name = "employee_roles",
         joinColumns = @JoinColumn(name = "employee_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
 )
 private List<Role> roles;

 public Employee(String username, int phoneNumber, String email, String password) {
  this.username = username;
  this.phoneNumber = phoneNumber;
  this.email = email;
  this.password = password;
  this.verified = false;
  this.deleted = false;
  this.locked = false;
  this.verificationCode=generateVerificationCode();
 this.resetPasswordVerification=generateResetPasswordVerificationCode();
 }

 public String generateVerificationCode() {
  return generateCode(6);
 }

 public String generateResetPasswordVerificationCode() {
  return generateCode(6); // Using the same length as verification code
 }

 private String generateCode(int codeLength) {
  final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  Random random = new Random();
  StringBuilder codeBuilder = new StringBuilder(codeLength);

  for (int i = 0; i < codeLength; i++) {
   int randomIndex = random.nextInt(CODE_CHARACTERS.length());
   codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
  }

  return codeBuilder.toString();
 }

 public boolean verifyCode(String code) {
  if (verificationCode.equals(code)) {
   LocalDateTime currentTime = LocalDateTime.now();
   LocalDateTime expiryTime = verificationTime.plusMinutes(5);
   return currentTime.isBefore(expiryTime);
  }
  return false;
 }

 public boolean verifyResetPasswordCode(String code) {
  if (resetPasswordVerification.equals(code)) {
   LocalDateTime currentTime = LocalDateTime.now();
   LocalDateTime expiryTime = resetVerificationTime.plusMinutes(3); // Expiry time for reset password verification is 5 minutes
   return currentTime.isBefore(expiryTime);
  }
  return false;
 }
}*/