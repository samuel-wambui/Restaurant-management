package FINALEQUIFARM.EQUIFARM.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
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

 @Column(name = "pfNumber")
 private int pfNumber;

 @Column(name = "email")
 private String email;

 @Column(name = "password")
 private String password;

 @Column(name = "isActive")
 private boolean isActive = false;

 @Column(name = "verificationCode")
 private String verificationCode= generateVerificationCode();


 @Column(name = "confirmVerification")
 private String confirmVerification;

 @Column(name = "isAdmin")
 private boolean isAdmin = true;

 @ManyToMany
 @JoinTable(
         name = "employee_roles",
         joinColumns = @JoinColumn(name = "employee_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
 )
 private List<Role> roles;

 // Constructor with parameters
 public Employee(String username, int pfNumber, String email, String password) {
  this.username = username;
  this.pfNumber = pfNumber;
  this.email = email;
  this.password = password;
  this.isActive = false;
  this.isAdmin = true;
 }

 // Method to generate verification code
 private String generateVerificationCode() {
  final int CODE_LENGTH = 6; // Length of the verification code
  final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Characters allowed in the code

  Random random = new Random();
  StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);

  // Generate random characters for the code
  for (int i = 0; i < CODE_LENGTH; i++) {
   int randomIndex = random.nextInt(CODE_CHARACTERS.length());
   codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
  }

  return codeBuilder.toString();
 }
}
