package HotelManagement.inventory;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllItems() {
        try {
            List<Inventory> items = inventoryService.getAllItems();
            return ResponseEntity.ok(new ApiResponse<>("Items retrieved successfully", 200, items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve items", 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> getItemById(@PathVariable Long id) {
        try {
            Inventory item = inventoryService.getItemById(id);
            return ResponseEntity.ok(new ApiResponse<>("Item retrieved successfully", 200, item));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve item", 500, null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> addItem(@RequestBody InventoryDto inventoryDto) {
        try {
            Inventory newItem = inventoryService.addItem(inventoryDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Item added successfully", 201, newItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to add item", 500, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> updateItem(@PathVariable Long id, @RequestBody InventoryDto inventoryDto) {
        try {
            Inventory updatedItem = inventoryService.updateItem(id, inventoryDto);
            return ResponseEntity.ok(new ApiResponse<>("Item updated successfully", 200, updatedItem));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update item", 500, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        try {
            inventoryService.deleteItem(id);
            return ResponseEntity.ok(new ApiResponse<>("Item deleted successfully", 200, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to delete item", 500, null));
        }
    }
}
