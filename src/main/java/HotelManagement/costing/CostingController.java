package HotelManagement.costing;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cost")
public class CostingController {
    @Autowired
    private CostingService costingService;
    @PostMapping("/createCost")
    public ResponseEntity<ApiResponse<Costing>> createCost(@RequestBody CostingDto costingDto) {
        ApiResponse<Costing> response = new ApiResponse<>();

        try {
            // Delegate to service to handle saving and validations
            Costing cost = costingService.saveCost(costingDto);
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
}
