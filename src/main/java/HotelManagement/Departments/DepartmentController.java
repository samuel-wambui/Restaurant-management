package HotelManagement.Departments;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("addDepartment")
    public ResponseEntity<ApiResponse> addDepartment(@RequestBody DepartmentDto departmentDto) {
        ApiResponse apiResponse = new ApiResponse();

        try {
            DepartmentEntity departmentEntity = departmentService.save(departmentDto);
            apiResponse.setMessage("Department added successfully");
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setEntity(departmentService.save(departmentDto));
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllDepartments")
    public ResponseEntity<ApiResponse<List<DepartmentEntity>>> getAllDepartments() {
        ApiResponse<List<DepartmentEntity>> apiResponse = new ApiResponse<>();
        try {
            List<DepartmentEntity> departmentEntities = departmentService.getAllDepartments();
            apiResponse.setMessage("Departments retrieved successfully");
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setEntity(departmentEntities);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
    @PostMapping("/updateDepartment/{id}")
    public ResponseEntity<ApiResponse<DepartmentEntity>> updateDepartment(@RequestBody DepartmentDto departmentDto, @RequestParam Long id) {
        ApiResponse<DepartmentEntity> apiResponse = new ApiResponse<>();
        try {
            DepartmentEntity departmentEntity = departmentService.update(departmentDto, id);
            apiResponse.setMessage("Department updated successfully");
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setEntity(departmentEntity);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
@DeleteMapping("deleteDepartment/{id}")
    public ResponseEntity<ApiResponse> deleteDepartment(@PathVariable Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            departmentService.deleteDepartment(id);
            apiResponse.setMessage("Department deleted successfully");
            apiResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}




