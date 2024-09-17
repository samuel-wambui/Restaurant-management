package HotelManagement.tables;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
public class TablesController {

    @Autowired
    private TablesService tablesService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TablesEntity>>> getAllTables() {
        try {
            List<TablesEntity> tables = tablesService.getAllTables();
            return ResponseEntity.ok(new ApiResponse<>("Tables retrieved successfully", 200, tables));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve tables", 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TablesEntity>> getTableById(@PathVariable Long id) {
        try {
            TablesEntity table = tablesService.getTableById(id);
            return ResponseEntity.ok(new ApiResponse<>("Table retrieved successfully", 200, table));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve table", 500, null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TablesEntity>> createTable(@RequestBody TablesDto tablesDto) {
        try {
            TablesEntity newTable = tablesService.createTable(tablesDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Table created successfully", 201, newTable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to create table", 500, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TablesEntity>> updateTable(@PathVariable Long id, @RequestBody TablesDto tablesDto) {
        try {
            TablesEntity updatedTable = tablesService.updateTable(id, tablesDto);
            return ResponseEntity.ok(new ApiResponse<>("Table updated successfully", 200, updatedTable));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update table", 500, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTable(@PathVariable Long id) {
        try {
            tablesService.deleteTable(id);
            return ResponseEntity.ok(new ApiResponse<>("Table deleted successfully", 200, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to delete table", 500, null));
        }
    }
}
