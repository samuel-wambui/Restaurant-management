package HotelManagement.recipe;

import HotelManagement.foodStock.FoodStock;
import HotelManagement.menu.Menu;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import HotelManagement.recipe.todayRecipe.OrderedRecipe;
import HotelManagement.spices.SpicesAndSeasonings;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipeName;
    private String recipeNumber;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_ingredients",  // Unique table name
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @JsonIgnore
    private Set<FoodStock> foodStockSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_spices",  // Unique table name
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "spice_id")
    )
    @JsonIgnore
    private Set<SpicesAndSeasonings> spicesSet = new HashSet<>();

    private String deletedFlag = "N";

    @ManyToMany(mappedBy = "recipeSet")
    @JsonIgnore
    private Set<OrderedRecipe> orderedRecipes = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_missing_clause_recipes",  // Unique table name
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "missing_clause_id")
    )
    private Set<MissingClauseRecipe> missingClauseRecipes = new HashSet<>();
}
