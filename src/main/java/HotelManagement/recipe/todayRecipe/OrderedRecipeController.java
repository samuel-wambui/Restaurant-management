package HotelManagement.recipe.todayRecipe;

import HotelManagement.ApiResponse.ApiResponse;
import HotelManagement.exemption.ResourceNotFoundException;
import HotelManagement.recipe.missingClause.MissingClauseDto;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import HotelManagement.recipe.missingClause.MissingClauseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class OrderedRecipeController {
    @Autowired
   OrderedRecipeService orderedRecipeService;
    public ResponseEntity<ApiResponse<OrderedRecipe>> createMissingClause(@RequestBody OrderedRecipeDto orderedRecipeDto) {
        try {
            ApiResponse response = new ApiResponse<>();
            OrderedRecipe orderedRecipe = orderedRecipeService.createOrderedRecipe(orderedRecipeDto);
            response.setMessage("Missing clause created successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(orderedRecipeDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            ApiResponse response = new ApiResponse<>();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse<>();
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }}