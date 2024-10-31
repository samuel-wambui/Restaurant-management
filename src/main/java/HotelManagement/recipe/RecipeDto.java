package HotelManagement.recipe;

import lombok.Data;

import java.util.Set;
@Data
public class RecipeDto {
    private String recipeName;
    private Set<Long> ingredientIds;
    private Set<Long> spiceIds;

}
