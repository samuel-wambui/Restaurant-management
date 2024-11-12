package HotelManagement.spices;

import HotelManagement.costing.Costing;
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
public class SpicesAndSeasonings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "spicesSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<Recipe> recipes = new HashSet<>();

    @ManyToMany(mappedBy = "spicesSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<MissingClauseRecipe> missingClauseRecipes = new HashSet<>();

    private String deletedFlag = "N";

}
