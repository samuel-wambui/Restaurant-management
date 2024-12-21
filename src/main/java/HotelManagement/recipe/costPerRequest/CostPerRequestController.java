package HotelManagement.recipe.costPerRequest;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.exemption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        catch (RuntimeException e){
            ApiResponse response = new ApiResponse<>();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }}

    @PostMapping("/updateCostPerRequestRecord/{id}")
    public ResponseEntity<ApiResponse<CostPerRequest>> updateCostPerRequest(
            @PathVariable Long id,
            @RequestBody CostPerRequestDto costPerRequest) {
        try {
            ApiResponse<CostPerRequest> response = new ApiResponse<>();

            // Call the service method to update the request
            CostPerRequest updatedRequest = requestService.updateRequestCost(id, costPerRequest);

            // Build the success response
            response.setMessage("Request updated successfully");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(updatedRequest);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Build the error response
            ApiResponse<CostPerRequest> response = new ApiResponse<>();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

@GetMapping("/getAllRequestPerCostRecords")
    public ResponseEntity<ApiResponse<List<CostPerRequest>>> getAllRequestPerCostRecords(){
    try {
        ApiResponse<List<CostPerRequest>> response = new ApiResponse<>();

        // Call the service method to update the request
        List<CostPerRequest> costPerRequestList = requestService.getAllRequestCosts();
        if(costPerRequestList.isEmpty()) {
            response.setMessage("No requests yet");
            response.setStatusCode(HttpStatus.NO_CONTENT.value());
        }
        // Build the success response
        response.setMessage("Requests");
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(costPerRequestList);
        return new ResponseEntity<>(response, HttpStatus.OK);



    } catch (RuntimeException e) {
        // Build the error response
        ApiResponse<List<CostPerRequest>> response = new ApiResponse<>();
        response.setMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
@GetMapping("getRequestById/{id}")
    public ResponseEntity<ApiResponse<CostPerRequest>> getRequestById(@PathVariable Long id){
    try {
        ApiResponse<CostPerRequest> response = new ApiResponse<>();

        // Call the service method to update the request
        CostPerRequest costPerRequest = requestService.getRequestCostById(id);

        // Build the success response
        response.setMessage("Request");
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(costPerRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);



    } catch (RuntimeException e) {
        // Build the error response
        ApiResponse<CostPerRequest> response = new ApiResponse<>();
        response.setMessage(e.getMessage());
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
@DeleteMapping("/deleteRequest")
    public ResponseEntity<ApiResponse<CostPerRequest>> deleteReQuest(@RequestBody Long id) {
    try {
        ApiResponse response = new ApiResponse<>();
        CostPerRequest deletedRequest = requestService.deleteCostPerRequest(id);
        response.setMessage("Request Deleted Successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (RuntimeException e) {
        // Build the error response
        ApiResponse<CostPerRequest> response = new ApiResponse<>();
        response.setMessage(e.getMessage());
        response.setStatusCode(HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}


}



