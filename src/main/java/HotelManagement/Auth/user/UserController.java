package HotelManagement.Auth.user;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.Auth.dto.UserRoleDTO;
import HotelManagement.EmailApp.EmailSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/employees")
public class UserController {

    private final UserService userService;

    private final EmailSender emailSender;
    public UserController(UserService userService, EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;

    }

    @GetMapping("/welcome")
    public String welcome() {
        return "WELCOME TO EQUIFARM";
    }




    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable("id") long employeeId) {
        try {
            User user = userService.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving employee: " + e.getMessage());
        }
    }

    @PostMapping("/assignRoles")
    public ResponseEntity<ApiResponse<User>> assignRoleToEmployee(@RequestBody UserRoleDTO employee) {
        try {
            ApiResponse response = new ApiResponse<>();
            User updatedUser = userService.assignRole(employee);
            response.setMessage("role successfully assigned to the user");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(updatedUser);
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
            userService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting employee: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllEmployees() {
        try {
            List<User> users = userService.findAllEmployees();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

