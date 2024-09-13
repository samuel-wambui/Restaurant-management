package HotelManagement.roles;

import HotelManagement.employee.Permissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
        private final RoleService roleService;

        @Autowired
        public RoleController(RoleService roleService) {
            this.roleService = roleService;
        }

        @PostMapping("/create")
        public Role createRole(@RequestParam String roleName, @RequestParam Set<Permissions> permissions, @RequestParam String postedBy) {
            return roleService.createNewRole(roleName, permissions, postedBy);
        }

        @PutMapping("/update/{roleId}")
        public Role updateRolePermissions(@PathVariable Long roleId, @RequestParam Set<Permissions> permissions) {
            return roleService.updateRolePermissions(roleId, permissions);
        }
    }


