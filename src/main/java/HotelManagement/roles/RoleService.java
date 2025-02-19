package HotelManagement.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
@Service
public class RoleService {


        private final RoleRepository roleRepository;

        @Autowired
        public RoleService(RoleRepository roleRepository) {
            this.roleRepository = roleRepository;
        }

        public Role createNewRole(String roleName, Set<Permissions> permissions, String postedBy) {
            Role role = new Role();
            role.setName(roleName);
            role.setPermissions(permissions);
            role.setPostedBy(postedBy);
            role.setPostedTime(new Date());
            return roleRepository.save(role);
        }

        public Role updateRolePermissions(Long roleId, Set<Permissions> newPermissions) {
            Role role = roleRepository.findByIdAndDeletedByNull(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
            role.setPermissions(newPermissions);
            return roleRepository.save(role);
        }
        public List<Role> getAllRoles(){
            return roleRepository.findByDeletedByNull();
        }
        public Role deleteRole(Long roleId) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
            role.setDeletedBy("admin");
            role.setDeletedTime(new Date());
            roleRepository.save(role);
            return role;
        }
    }


