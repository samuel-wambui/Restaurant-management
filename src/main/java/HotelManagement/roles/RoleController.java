package HotelManagement.roles;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@RestController
//@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
//    @PreAuthorize("hasAuthority('ADMIN_CREATE')")

    public ResponseEntity <ApiResponse> createRole(@RequestParam String roleName, @RequestParam Set<Permissions> permissions,
                                                   @RequestParam String postedBy) {
        ApiResponse response = new ApiResponse();
        try {
            Role newRole = roleService.createNewRole(roleName, permissions, postedBy);
            response.setMessage("Role created successfully");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(newRole);
        }
        catch (Exception e){
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @PutMapping("/update/{roleId}")
//    @PreAuthorize("hasAuthority('ADMIN_UPDATE')")

    public ResponseEntity<ApiResponse> updateRolePermissions(@PathVariable Long roleId, @RequestParam Set<Permissions> permissions) {
        ApiResponse response = new ApiResponse();
        try {
            Role updatedRole = roleService.updateRolePermissions(roleId, permissions);
            response.setMessage("Role permissions updated successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(updatedRole);

        }
        catch (Exception e){
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAll")
//    @PreAuthorize("hasAuthority('ADMIN_READ')")
    ResponseEntity<ApiResponse> getAllRoles() {
        ApiResponse response = new ApiResponse();
        try {
            List<Role> roles = roleService.getAllRoles();
            response.setMessage("Fetched all roles successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(roles);

        }
        catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
public  ResponseEntity<ApiResponse> deleteRole(@PathVariable Long roleId) {
        ApiResponse response = new ApiResponse();
        try {
            roleService.deleteRole(roleId);
            response.setMessage("Role deleted successfully");
            response.setStatusCode(HttpStatus.OK.value());
        }
        catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


