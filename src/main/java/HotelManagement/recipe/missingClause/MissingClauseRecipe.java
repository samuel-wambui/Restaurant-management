package HotelManagement.recipe.missingClause;

import HotelManagement.foodStock.FoodStock;
import HotelManagement.menu.Menu;
import HotelManagement.recipe.Recipe;
import HotelManagement.spices.SpicesAndSeasonings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MissingClauseRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String missingClauseName;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "missingClause_ingredients",
            joinColumns = @JoinColumn(name = "missingClause_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @JsonIgnore // Ignore serialization to prevent recursion
    private Set<FoodStock> foodStockSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "missingClause_spices",
            joinColumns = @JoinColumn(name = "missingClause_id"),
            inverseJoinColumns = @JoinColumn(name = "missingClause_id")
    )
    @JsonIgnore // Ignore serialization to prevent recursion
    private Set<SpicesAndSeasonings> spicesSet = new HashSet<>();

    private String deletedFlag = "N";

    @ManyToMany(mappedBy = "missingClauseRecipes")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<Recipe> recipes = new HashSet<>();
}
