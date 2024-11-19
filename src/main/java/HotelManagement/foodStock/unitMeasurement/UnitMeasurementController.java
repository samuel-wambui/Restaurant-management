package HotelManagement.foodStock.unitMeasurement;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unitMeasurement")
public class UnitMeasurementController {
    @Autowired
    UnitMeasurementService unitMeasurementService;

    @PostMapping("/createUnit")
    public ResponseEntity<ApiResponse<UnitMeasurement>> createUnitMeasurement(@RequestBody UnitMeasurementDto unitMeasurementDto) {
        try {
            ApiResponse response = new ApiResponse<>();
            UnitMeasurement unitMeasurement = unitMeasurementService.createUnitMeasurement(unitMeasurementDto);
            response.setMessage("Unit added successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(unitMeasurement);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (RuntimeException e){
            ApiResponse response = new ApiResponse<>();
            response.setMessage("an error occurred please try later");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
}
