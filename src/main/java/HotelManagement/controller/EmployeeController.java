package HotelManagement.controller;
import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.model.Employee;


import HotelManagement.model.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address cannot be null");
            }

            if (model.getText() == null || model.getText().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email text cannot be null");
            }

            employee.generateVerificationCode();
            Employee savedEmployee = employeeService.saveEmployee(employee);

            String toEmail = employee.getEmail();
            String text = "Hello " + savedEmployee.getUsername() + ", your verification code is " + savedEmployee.getVerificationCode() + ". Thank you!";
            emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save employee: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmployee(@RequestParam Long id, @RequestParam String verificationCode) {
        try {
            Employee employee = employeeService.findById(id).orElse(null);

            if (employee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            }

            if (employee.verifyCode(verificationCode)) {
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime expiryTime = employee.getVerificationTime().plusMinutes(2); // Expiry time is 2 minutes after generation
                if (currentTime.isAfter(expiryTime)) {
                    employeeService.deleteEmployee(id);
                    return ResponseEntity.badRequest().body("Verification code has expired");
                }

                employee.setVerifiedFlag(true);
                employeeService.updateEmployee(employee);

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

//    @GetMapping
//    public ResponseEntity<List<Employee>> getAllEmployees() {
//        try {
//            List<Employee> employees = employeeService.findAll();
//            return ResponseEntity.ok(employees);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") long employeeId) {
        try {
            Employee employee = employeeService.findById(employeeId).orElse(null);
            if (employee != null) {
                return ResponseEntity.ok(employee);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employee: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") long id, @RequestBody Employee employee) {
        try {
            Employee existingEmployee = employeeService.findById(id).orElse(null);
            if (existingEmployee != null) {
                employee.setId(id);
                Employee updatedEmployee = employeeService.updateEmployee(employee);
                return ResponseEntity.ok(updatedEmployee);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting employee: " + e.getMessage());
        }
    }
}
