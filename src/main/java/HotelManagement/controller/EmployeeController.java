package HotelManagement.controller;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.employee.EmployeeDTO;
import HotelManagement.employee.EmployeeService;
import HotelManagement.roles.Erole;
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
    public ResponseEntity<?> saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        try {
            if (employeeDTO.getEmail() == null || employeeDTO.getEmail().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address cannot be null or empty");
            }

            EmployeeDTO savedEmployee = employeeService.saveEmployee(employeeDTO);

            String toEmail = savedEmployee.getEmail();
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
            EmployeeDTO employeeDTO = employeeService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expiryTime = employeeDTO.getVerificationTime().plusMinutes(5);

            if (currentTime.isAfter(expiryTime)) {
                employeeService.deleteEmployee(id);
                return ResponseEntity.badRequest().body("Verification code has expired");
            }

            if (employeeService.verifyCode(id, verificationCode)) {
                employeeDTO.setVerifiedFlag("Y");
                employeeService.updateEmployee(employeeDTO);

                String toEmail = employeeDTO.getEmail();
                String text = "Dear " + employeeDTO.getUsername() + ", your account has been successfully verified. Thank you!";
                emailSender.sendEmailWithVerificationCode(toEmail, model.getSubject(), text);

                return ResponseEntity.ok("User verified successfully");
            } else {
                return ResponseEntity.badRequest().body("Verification code does not match");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to verify employee: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") long employeeId) {
        try {
            EmployeeDTO employeeDTO = employeeService.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            return ResponseEntity.ok(employeeDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employee: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") long id, @RequestBody EmployeeDTO employeeDTO) {
        try {
            employeeDTO.setId(id);
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating employee: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting employee: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        try {
            List<EmployeeDTO> employees = employeeService.findAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesBySupervisor(@PathVariable Long supervisorId) {
        try {
            List<EmployeeDTO> employees = employeeService.getEmployeesBySupervisor(supervisorId);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{supervisorId}/assign/{employeeId}")
    public ResponseEntity<?> assignEmployeeToSupervisor(@PathVariable Long supervisorId, @PathVariable Long employeeId) {
        try {
            employeeService.assignSupervisedEmployee(supervisorId, employeeId);
            return ResponseEntity.ok("Employee assigned to supervisor successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error assigning employee to supervisor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/assign-shift")
    public ResponseEntity<?> assignShiftToSupervisor(@PathVariable Long id, @RequestParam String shiftSchedule) {
        try {
            employeeService.assignShiftToSupervisor(id, shiftSchedule);
            return ResponseEntity.ok("Shift schedule assigned to supervisor successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error assigning shift schedule: " + e.getMessage());
        }
    }
}
