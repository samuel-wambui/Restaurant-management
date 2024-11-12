package HotelManagement.recipe.missingClause;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/missingClause")
public class MissingClauseController {
    @Autowired
    MissingClauseService missingClauseService;

    @PostMapping("/createMissingClause")
   public ResponseEntity<ApiResponse<MissingClauseRecipe>> createMissingClause(@RequestBody MissingClauseDto missingClauseDto){
       try {
           ApiResponse response =new ApiResponse<>();
           MissingClauseRecipe missingClauseRecipe = missingClauseService.createMissingClause(missingClauseDto);
           response.setMessage("missing clause created");
           response.setStatusCode(HttpStatus.OK.value());
           response.setEntity(missingClauseRecipe);
           return new ResponseEntity<>(response, HttpStatus.CREATED);

       }
       catch (RuntimeException e){
           ApiResponse response =new ApiResponse<>();
           response.setMessage("a server error occurred please try agan later");
           response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

       }
   }


}
