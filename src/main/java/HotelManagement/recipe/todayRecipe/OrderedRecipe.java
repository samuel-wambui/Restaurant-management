package HotelManagement.recipe.todayRecipe;

import HotelManagement.menu.Menu;
import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class OrderedRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipe;
    private String orderedRecipeName;
    private String orderedRecipeNumber;
    private String deletedFlag;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "ordered_recipe_missing_clause",  // Unique table name
            joinColumns = @JoinColumn(name = "ordered_recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "missing_clause_id")
    )
    @JsonIgnore
    private Set<MissingClauseRecipe> missingClauseRecipes = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "ordered_recipe_recipes",  // Unique table name
            joinColumns = @JoinColumn(name = "ordered_recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    @JsonIgnore
    private Set<Recipe> recipeSet = new HashSet<>();

    @ManyToMany(mappedBy = "orderedRecipes")
    @JsonIgnore
    private Set<Menu> menuSet = new HashSet<>();

}
