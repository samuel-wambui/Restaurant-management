package HotelManagement.controller;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import HotelManagement.employee.Employee;
import HotelManagement.employee.EmployeeDTO;
import HotelManagement.employee.EmployeeRoleDTO;
import HotelManagement.employee.EmployeeService;
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
    private final EmployeeService employeeServiceE;

    public EmployeeController(EmployeeService employeeService, Model model, EmailSender emailSender, EmployeeService employeeServiceE) {
        this.employeeService = employeeService;
        this.model = model;
        this.emailSender = emailSender;
        this.employeeServiceE = employeeServiceE;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "WELCOME TO EQUIFARM";
    }

    @PostMapping
    public ResponseEntity<?> saveEmployee(@RequestBody Employee employee) {
        try {
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address cannot be null or empty");
            }

            Employee savedEmployee = employeeService.saveEmployee(employee);

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
            Employee employee = employeeService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expiryTime = employee.getVerificationTime().plusMinutes(5);

            if (currentTime.isAfter(expiryTime)) {
                employeeService.deleteEmployee(id);
                return ResponseEntity.badRequest().body("Verification code has expired");
            }

            if (employeeService.verifyCode(id, verificationCode)) {
                employee.setVerifiedFlag(true);
                //employeeService.updateEmployee(employee);

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") long employeeId) {
        try {
            Employee employee = employeeService.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employee: " + e.getMessage());
        }
    }

    @PostMapping("/assignRoles")
    public ResponseEntity<ApiResponse<Employee>> assignRoleToEmployee(@RequestBody EmployeeRoleDTO employee) {
        try {
            ApiResponse response = new ApiResponse<>();
            Employee updatedEmployee = employeeService.assignRole(employee);
            response.setMessage("role successfully assigned to the user");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(updatedEmployee);
            return new ResponseEntity (response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse<>();
            response.setMessage("Internal Server Error");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = employeeServiceE.findAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

//    @GetMapping("/supervisor/{supervisorId}")
//    public ResponseEntity<List<Employee>> getEmployeesBySupervisor(@PathVariable Long supervisorId) {
//        try {
//            List<Employee> employees = employeeService.getEmployeesBySupervisor(supervisorId);
//            return ResponseEntity.ok(employees);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//
//    @PostMapping("/{supervisorId}/assign/{employeeId}")
//    public ResponseEntity<?> assignEmployeeToSupervisor(@PathVariable Long supervisorId, @PathVariable Long employeeId) {
//        try {
//            employeeService.assignSupervisedEmployee(supervisorId, employeeId);
//            return ResponseEntity.ok("Employee assigned to supervisor successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error assigning employee to supervisor: " + e.getMessage());
//        }
//    }
//
//    @PutMapping("/{id}/assign-shift")
//    public ResponseEntity<?> assignShiftToSupervisor(@PathVariable Long id, @RequestParam String shiftSchedule) {
//        try {
//            employeeService.assignShiftToSupervisor(id, shiftSchedule);
//            return ResponseEntity.ok("Shift schedule assigned to supervisor successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error assigning shift schedule: " + e.getMessage());
//        }
//    }
//}
