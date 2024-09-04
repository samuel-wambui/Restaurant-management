package HotelManagement.manager.managercontroller;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.manager.ManagerDto;
import HotelManagement.manager.ManagerEntity;
import HotelManagement.manager.managerservice.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/managers")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<ManagerEntity>>> getAllManagers() {
        try {
            List<ManagerEntity> managers = managerService.getAllManagers();
            return ResponseEntity.ok(new ApiResponse<>("Managers fetched successfully", HttpStatus.OK.value(), managers));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponse<ManagerEntity>> getManagerById(@PathVariable Long id) {
        try {
            ManagerEntity manager = managerService.getManagerById(id);
            return ResponseEntity.ok(new ApiResponse<>("Manager fetched successfully", HttpStatus.OK.value(), manager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Manager not found", HttpStatus.NOT_FOUND.value(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ManagerEntity>> createManager(@RequestBody ManagerDto managerDto) {
        try {
            ManagerEntity manager = managerService.createManager(managerDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Manager created successfully", HttpStatus.CREATED.value(), manager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Invalid request", HttpStatus.BAD_REQUEST.value(), null));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ManagerEntity>> updateManager(@PathVariable Long id, @RequestBody ManagerDto managerDto) {
        try {
            ManagerEntity manager = managerService.updateManager(id, managerDto);
            return ResponseEntity.ok(new ApiResponse<>("Manager updated successfully", HttpStatus.OK.value(), manager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Manager not found", HttpStatus.NOT_FOUND.value(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteManager(@PathVariable Long id) {
        try {
            managerService.deleteManager(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>("Manager deleted successfully", HttpStatus.NO_CONTENT.value(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Manager not found", HttpStatus.NOT_FOUND.value(), null));
        }
    }
}
