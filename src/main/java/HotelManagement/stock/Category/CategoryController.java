package HotelManagement.stock.Category;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create/category")
    public ResponseEntity<ApiResponse<Category>> createCategory(CategoryDto categoryDto) {
        ApiResponse<Category> response = new ApiResponse<>();
        try {
            Category category = categoryService.createCategory(categoryDto);
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setMessage("Category created successfully");
            response.setEntity(category);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);

        }
    }
@PostMapping("/updateCategory/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable Long id) {
        ApiResponse<Category> response = new ApiResponse<>();
        try {
            Category category = categoryService.updateCategory(categoryDto, id);
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Category updated successfully");
            response.setEntity(category);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);

        }
    }

    @GetMapping("/getAllCategory")
    public ResponseEntity<ApiResponse<List<Category>>> getallCategory() {
        ApiResponse<List<Category>> response = new ApiResponse<>();
        try {
            List<Category> category = categoryService.getAllCategory();
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Category updated successfully");
            response.setEntity(category);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(response);

        }
    }
        @DeleteMapping("/deleteCategory/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
            ApiResponse<Void> response = new ApiResponse<>();
            try {
                categoryService.deleteCategory(id);
                response.setMessage("Category deleted successfully");
                response.setStatusCode(HttpStatus.OK.value());
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException e) {
                response.setMessage(e.getMessage());
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.badRequest().body(response);
            }
        }
}
