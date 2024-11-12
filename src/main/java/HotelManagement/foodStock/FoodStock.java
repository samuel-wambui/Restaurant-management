package HotelManagement.foodStock;

import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FoodStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String unit;
    @ManyToMany(mappedBy = "foodStockSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<Recipe> recipes = new HashSet<>();

    @ManyToMany(mappedBy = "foodStockSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<MissingClauseRecipe> missingClauseRecipes = new HashSet<>();


    private String deletedFlag = "N";


}
