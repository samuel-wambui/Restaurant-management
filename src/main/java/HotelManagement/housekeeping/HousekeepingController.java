package HotelManagement.housekeeping;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/housekeeping")
public class HousekeepingController {

    @Autowired
    private HousekeepingService housekeepingService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<Housekeeping>>> getAllHousekeepingRecords() {
        try {
            List<Housekeeping> records = housekeepingService.getAllHousekeepingRecords();
            return ResponseEntity.ok(new ApiResponse<>("Housekeeping records retrieved successfully", 200, records));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve housekeeping records", 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Housekeeping>> getHousekeepingById(@PathVariable Long id) {
        try {
            Housekeeping record = housekeepingService.getHousekeepingById(id);
            return ResponseEntity.ok(new ApiResponse<>("Housekeeping record retrieved successfully", 200, record));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve housekeeping record", 500, null));
        }
    }

    @PostMapping("Create")
    public ResponseEntity<ApiResponse<Housekeeping>> createHousekeeping(@RequestBody HousekeepingDto housekeepingDto) {
        try {
            Housekeeping newRecord = housekeepingService.createHousekeeping(housekeepingDto);
            return ResponseEntity.status(201)
                    .body(new ApiResponse<>("Housekeeping record created successfully", 201, newRecord));
        } catch (ParseException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("Invalid date format", 400, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to create housekeeping record", 500, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Housekeeping>> updateHousekeeping(@PathVariable Long id, @RequestBody HousekeepingDto housekeepingDto) {
        try {
            Housekeeping updatedRecord = housekeepingService.updateHousekeeping(id, housekeepingDto);
            return ResponseEntity.ok(new ApiResponse<>("Housekeeping record updated successfully", 200, updatedRecord));
        } catch (ParseException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<>("Invalid date format", 400, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to update housekeeping record", 500, null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteHousekeeping(@PathVariable Long id) {
        try {
            housekeepingService.deleteHousekeeping(id);
            return ResponseEntity.ok(new ApiResponse<>("Housekeeping record deleted successfully", 200, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to delete housekeeping record", 500, null));
        }
    }
}
