package FINALEQUIFARM.EQUIFARM.controller;



import FINALEQUIFARM.EQUIFARM.EmailApp.EmailSender;
import FINALEQUIFARM.EQUIFARM.EmailApp.Model;
import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.repository.EmployeeRepository;
import FINALEQUIFARM.EQUIFARM.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final Model model;
    private final EmailSender emailSender;
    private final Employee employee;
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeService employeeService, Model model, EmailSender emailSender, Employee employee, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.model = model;
        this.emailSender = emailSender;
        this.employee = employee;
        this.employeeRepository = employeeRepository;
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
                // Update the employee's status to active
                employee.setActive(true);
                employeeService.saveEmployee(employee);
                // Save the updated employee back to the database
                String toEmail = employee.getEmail();
                String text =  "Dear "+ employee.getUsername() + " your account has been successively verified. Thank you!";
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


/*@PostMapping("/verify")
    public ResponseEntity<String> saveConfirmVerificationCode(@RequestParam long userId, @RequestParam String verificationCode) {
        // Retrieve the employee from the database
        Optional<Employee> optionalEmployee = employeeRepository.findById(userId);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();

            // Check if the provided verification code matches the employee's verification code
            if (verificationCode.equals(employee.getVerificationCode())) {
                // Set the employee as active
                employee.setActive(true);

                // Save the updated employee in the database
                employeeRepository.save(employee);

                return ResponseEntity.ok("User verified successfully");
            } else {
                // Verification code doesn't match
                return ResponseEntity.badRequest().body("Verification code does not match");
            }
        } else {
            // Employee not found
            return ResponseEntity.notFound().build();
        }
    }
}*/