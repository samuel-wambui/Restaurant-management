package HotelManagement.recipe.costPerRequest;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.exemption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costPerRequest")
public class CostPerRequestController {
    @Autowired
    RequestService requestService;
    @PostMapping("/createRequest")
    public ResponseEntity<ApiResponse<CostPerRequest>> createCostPerRequest(@RequestBody CostPerRequestDto costPerRequestDto){
        try {
            ApiResponse response = new ApiResponse<>();
            CostPerRequest costPerRequest = requestService.createRequestCost(costPerRequestDto);
            response.setMessage("request created");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(costPerRequest);
            return new ResponseEntity<>(response,HttpStatus.CREATED);

        }
        catch (ResourceNotFoundException e){
            ApiResponse response = new ApiResponse<>();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }
    }
}
