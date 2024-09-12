package HotelManagement.employee;

import HotelManagement.roles.Erole;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
 private LocalDateTime verificationTime;

 @Column(name = "verified_flag", nullable = false)
 private String verifiedFlag = "N";

 @Column(name = "locked_flag", nullable = false)
 private String lockedFlag = "N";

 @Column(name = "reset_password_verification")
 private String resetPasswordVerification;

 @Column(name = "reset_verification_time")
 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
 private LocalDateTime resetVerificationTime;

 @Column(name = "deleted_flag", nullable = false)
 private String deletedFlag = "N";

 @ElementCollection(targetClass = Erole.class)
 @Enumerated(EnumType.STRING)
 @CollectionTable(name = "employee_roles", joinColumns = @JoinColumn(name = "employee_id"))
 @Column(name = "role")
 private List<Erole> roles;

 @Column(name = "assigned_tables")
 private String assignedTables;  // Waiter's assigned tables

 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
 @Column(name = "shift_start_time")
 private LocalDateTime shiftStartTime;

 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
 @Column(name = "shift_end_time")
 private LocalDateTime shiftEndTime;

 @OneToMany
 @JoinColumn(name = "supervised_employees")
 private List<Employee> supervisedEmployees;  // Employees under supervisor's management

 @Column(name = "shift_schedule")
 private String shiftSchedule;  // Supervisor's shift schedule

 // Constructor for creating new employees without extra fields
 public Employee(String username, String phoneNumber, String email, String password, List<Erole> roles) {
  this.username = username;
  this.phoneNumber = phoneNumber;
  this.email = email;
  this.password = password;
  this.roles = roles;
  this.verifiedFlag = "N";
  this.deletedFlag = "N";
  this.lockedFlag = "N";
  generateVerificationCode();
 }



 // Automatically generates a 6-character verification code
 public void generateVerificationCode() {
  this.verificationCode = generateCode(6);
  this.verificationTime = LocalDateTime.now();
 }

 // Generates a reset password code
 public String generateResetPasswordVerificationCode() {
  this.resetPasswordVerification = generateCode(6);
  this.resetVerificationTime = LocalDateTime.now();
  return this.resetPasswordVerification;
 }

 // Verifies if the provided code matches the stored one and is still valid (within 5 minutes)
 public boolean verifyCode(String code) {
  if (verificationCode.equals(code)) {
   LocalDateTime currentTime = LocalDateTime.now();
   return currentTime.isBefore(verificationTime.plusMinutes(5));
  }
  return false;
 }

 // Method to assign tables to a waiter
 public void assignTables(String tables) {
  this.assignedTables = tables;
 }

 // Method to set shift times for waiters
 public void setShiftTimes(LocalDateTime start, LocalDateTime end) {
  this.shiftStartTime = start;
  this.shiftEndTime = end;
 }

 // Method to determine if the waiter is on shift
 public boolean isOnShift(LocalDateTime currentTime) {
  return currentTime.isAfter(shiftStartTime) && currentTime.isBefore(shiftEndTime);
 }

 // Method to manage supervised employees for supervisors
 public void addSupervisedEmployee(Employee employee) {
  this.supervisedEmployees.add(employee);
 }

 public void removeSupervisedEmployee(Employee employee) {
  this.supervisedEmployees.remove(employee);
 }

 public void assignShiftSchedule(String schedule) {
  this.shiftSchedule = schedule;
 }

 // Utility method to generate a random code
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
}
