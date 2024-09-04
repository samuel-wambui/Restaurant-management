package HotelManagement.manager.managerservice;

import HotelManagement.manager.ManagerDto;
import HotelManagement.manager.ManagerEntity;
import HotelManagement.manager.ManagerRepository;
import HotelManagement.roles.Erole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    public List<ManagerEntity> getAllManagers() {
        return managerRepository.findAll();
    }

    public ManagerEntity getManagerById(Long id) {
        try {
            return managerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving manager with id: " + id, e);
        }
    }

    public ManagerEntity createManager(ManagerDto managerDto) {
        try {
            ManagerEntity manager = new ManagerEntity();
            manager.setFirstname(managerDto.getFirstname());
            manager.setLastName(managerDto.getLastName());
            manager.setUsername(managerDto.getUsername());
            manager.setPassword(managerDto.getPassword());
            manager.setEmail(managerDto.getEmail());
            manager.setPhoneNumber(managerDto.getPhoneNumber());
            manager.setRole(Erole.ROLE_MANAGER);

            return managerRepository.save(manager);
        } catch (Exception e) {
            throw new RuntimeException("Error creating manager", e);
        }
    }

    public ManagerEntity updateManager(Long id, ManagerDto managerDto) {
        try {
            ManagerEntity manager = managerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));

            manager.setFirstname(managerDto.getFirstname());
            manager.setLastName(managerDto.getLastName());
            manager.setUsername(managerDto.getUsername());
            manager.setPassword(managerDto.getPassword());
            manager.setEmail(managerDto.getEmail());
            manager.setPhoneNumber(managerDto.getPhoneNumber());

            return managerRepository.save(manager);
        } catch (Exception e) {
            throw new RuntimeException("Error updating manager with id: " + id, e);
        }
    }

    public void deleteManager(Long id) {
        try {
            ManagerEntity manager = managerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));
            managerRepository.delete(manager);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting manager with id: " + id, e);
        }
    }
}
