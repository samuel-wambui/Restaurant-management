/*package FINALEQUIFARM.EQUIFARM.controller;

import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.model.Role;
import FINALEQUIFARM.EQUIFARM.repository.EmployeeRepository;
import FINALEQUIFARM.EQUIFARM.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/{userId}/assign-roles")
    public ResponseEntity<?> assignRolesToUser(@PathVariable("userId") Long userId,
                                                  @RequestBody List<Role> roles,
                                                  Authentication authentication) {
        // Get the authenticated user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Load the user making the request from the database
        boolean requestingEmployee = employeeRepository.findByUsernameAndIsDeletedFalse(userDetails.getUsername());

        // Check if the requesting user has permission to assign roles
        if (!requestingEmployee) {
            requestingEmployee.isAdmin();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to assign roles.");

        // Load the user to whom roles will be assigned
    }
}
*/
