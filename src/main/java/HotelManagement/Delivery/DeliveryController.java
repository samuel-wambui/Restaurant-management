package HotelManagement.Delivery;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Delivery>> createDelivery(@PathVariable Long orderId, @RequestBody Delivery delivery) {
        try {
            Delivery newDelivery = deliveryService.createDelivery(orderId, delivery);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Delivery created successfully", HttpStatus.CREATED.value(), newDelivery));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Failed to create delivery: " + e.getMessage(), HttpStatus.NOT_FOUND.value(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Delivery>> getDeliveryById(@PathVariable Long id) {
        Optional<Delivery> delivery = deliveryService.getDeliveryById(id);
        return delivery.map(d -> ResponseEntity.ok(new ApiResponse<>("Delivery found", HttpStatus.OK.value(), d)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Delivery not found", HttpStatus.NOT_FOUND.value(), null)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Delivery>>> getAllDeliveries() {
        List<Delivery> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(new ApiResponse<>("Deliveries retrieved successfully", HttpStatus.OK.value(), deliveries));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Delivery>>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>("Deliveries retrieved successfully", HttpStatus.OK.value(), deliveries));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Delivery>> updateDeliveryStatus(@PathVariable Long id, @RequestParam DeliveryStatus status) {
        try {
            Delivery updatedDelivery = deliveryService.updateDeliveryStatus(id, status);
            return ResponseEntity.ok(new ApiResponse<>("Delivery status updated", HttpStatus.OK.value(), updatedDelivery));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Failed to update delivery: " + e.getMessage(), HttpStatus.NOT_FOUND.value(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDelivery(@PathVariable Long id) {
        try {
            deliveryService.deleteDelivery(id);
            return ResponseEntity.ok(new ApiResponse<>("Delivery deleted successfully", HttpStatus.OK.value(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Failed to delete delivery: " + e.getMessage(), HttpStatus.NOT_FOUND.value(), null));
        }
    }
}
