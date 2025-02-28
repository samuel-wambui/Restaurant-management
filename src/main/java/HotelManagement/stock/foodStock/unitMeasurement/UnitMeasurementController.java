package HotelManagement.stock.foodStock.unitMeasurement;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/updateUnit/{id}")

    public ResponseEntity<ApiResponse<UnitMeasurement>> updateUnitMeasurement(@RequestBody UnitMeasurementDto unitMeasurementDto, @PathVariable Long id){
        ApiResponse<UnitMeasurement> response = new ApiResponse<>();
        try{
            UnitMeasurement unitMeasurement = unitMeasurementService.updateUnitmeasurement(id, unitMeasurementDto);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("unit measurement updated successfully");
            response.setEntity(unitMeasurement);
            return new ResponseEntity<>(response, HttpStatus.OK );
        }
        catch (RuntimeException e){
            response.setMessage("an error occurred please try later");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }


        @GetMapping("/getAllUnits")
public ResponseEntity<ApiResponse<List<UnitMeasurement>>> getAllMeasurementUnits(){

            ApiResponse<List<UnitMeasurement>> response = new ApiResponse<>() ;
            try {
                List<UnitMeasurement> unitMeasurementList = unitMeasurementService.getAllUnits();
                response.setMessage("Unit measurement fetched successfully");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(unitMeasurementList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            catch (RuntimeException e){
                response.setMessage("an error occurred please try later");
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR );
            }

        }
        @DeleteMapping("/deleteUnit/{Id}")
    public ResponseEntity<ApiResponse> deleteUnitMeasurement(@PathVariable Long id){
        ApiResponse response = new ApiResponse<>();
        try {
            unitMeasurementService.deleteUni(id);
            response.setMessage("Unit measurement deleted successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (RuntimeException e){
            response.setMessage("an error occurred please try later");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR );
        }
        }



    }

