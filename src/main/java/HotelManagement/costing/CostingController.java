package HotelManagement.costing;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cost")
public class CostingController {
    @Autowired
    private CostingService costingService;
    @PostMapping("/createCost")
    public ResponseEntity<ApiResponse<Costing>> createCost(@RequestBody CostingDto costingDto) {
        System.out.println("Incoming CostingDto: " + costingDto);

        ApiResponse<Costing> response = new ApiResponse<>();

        try {
            // Delegate to service to handle saving and validations
            Costing cost = costingService.saveFoodStockCost(costingDto);
            response.setMessage("Cost saved successfully");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(cost);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Handle any validation errors thrown by the service
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Costing>>> getAllCosts() {
        ApiResponse<List<Costing>> response = new ApiResponse<>();
        List<Costing> costs = costingService.findAllCosts();
        response.setMessage("Fetched all costs successfully");
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(costs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<Costing>> getCostById(@PathVariable Long id) {
        ApiResponse<Costing> response = new ApiResponse<>();

        try {
            Costing cost = costingService.getCostById(id);
            response.setMessage("Cost fetched successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(cost);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCost(@PathVariable Long id) {
        ApiResponse<Void> response = new ApiResponse<>();

        try {
            costingService.deleteCost(id);
            response.setMessage("Cost deleted successfully");
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
