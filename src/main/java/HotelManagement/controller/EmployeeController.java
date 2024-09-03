package HotelManagement.controller;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.model.Employee;
import HotelManagement.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final Model model;
    private final EmailSender emailSender;

    public EmployeeController(EmployeeService employeeService, Model model, EmailSender emailSender) {
        this.employeeService = employeeService;
        this.model = model;
        this.emailSender = emailSender;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "WELCOME TO EQUIFARM";
    }

    @PostMapping
    public ResponseEntity<?> saveEmployee(@RequestBody Employee employee) {
        try {
            // Check if the email address is provided
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address cannot be null");
            }

            // Check if the text for the email is provided
            if (model.getText() == null || model.getText().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email text cannot be null");
            }

            // Generate verification code and set verification time
            employee.generateVerificationCode();

            // Save the employee
            Employee savedEmployee = employeeService.saveEmployee(employee);

            // Send email with verification code
            String toEmail = employee.getEmail();
            String text = "Hello " + savedEmployee.getUsername() + " your verification code is " + savedEmployee.getVerificationCode() + ". Thank you!";
            emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save employee: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmployee(@RequestParam Long id, @RequestParam String verificationCode) {
        try {
            // Retrieve the employee by ID
            Employee employee = employeeService.getEmployeeById(id);

            // Check if the verification code matches
            if (verificationCode.equals(employee.getVerificationCode())) {
                // Check if the verification code is expired
                LocalDateTime verificationTime = employee.getVerificationTime();
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime expiryTime = verificationTime.plusMinutes(2); // Expiry time is 5 minutes after generation
                if (currentTime.isAfter(expiryTime)) {
                    employeeService.deleteEmployee(id);
                    return ResponseEntity.badRequest().body("Verification code has expired");
                }

                // Update the employee's status to active
                employee.setVerified(true);
                employeeService.saveEmployee(employee);

                // Send email to confirm verification
                String toEmail = employee.getEmail();
                String text = "Dear " + employee.getUsername() + ", your account has been successfully verified. Thank you!";
                emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

                return ResponseEntity.ok("User verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Verification code does not match");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to verify employee: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") long employeeId) {
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") long id, @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(employee, id);
        return ResponseEntity.ok(updatedEmployee);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
