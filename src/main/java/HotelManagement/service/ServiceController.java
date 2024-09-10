package HotelManagement.service;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("GetAll")
    public ResponseEntity<ApiResponse<List<Service>>> getAllServices() {
        try {
            List<HotelManagement.service.Service> services = serviceService.getAllServices();
            return ResponseEntity.ok(new ApiResponse<>("Services retrieved successfully", 200, services));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve services", 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelManagement.service.Service>> getServiceById(@PathVariable Long id) {
        try {
            HotelManagement.service.Service service = serviceService.getServiceById(id);
            return ResponseEntity.ok(new ApiResponse<>("Service retrieved successfully", 200, service));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve service", 500, null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HotelManagement.service.Service>> createService(@RequestBody ServiceDto serviceDto) {
        try {
            HotelManagement.service.Service newService = serviceService.createService(serviceDto);
            return ResponseEntity.status(201)
                    .body(new ApiResponse<>("Service created successfully", 201, newService));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to create service", 500, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelManagement.service.Service>> updateService(@PathVariable Long id, @RequestBody ServiceDto serviceDto) {
        try {
            HotelManagement.service.Service updatedService = serviceService.updateService(id, serviceDto);
            return ResponseEntity.ok(new ApiResponse<>("Service updated successfully", 200, updatedService));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to update service", 500, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.ok(new ApiResponse<>("Service deleted successfully", 200, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to delete service", 500, null));
        }
    }
}
