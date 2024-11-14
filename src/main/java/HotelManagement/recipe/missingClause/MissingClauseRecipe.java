package HotelManagement.recipe.missingClause;

import HotelManagement.foodStock.FoodStock;
import HotelManagement.menu.Menu;
import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.todayRecipe.OrderedRecipe;
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
            name = "missing_clause_ingredients",  // Unique table name
            joinColumns = @JoinColumn(name = "missing_clause_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @JsonIgnore
    private Set<FoodStock> foodStockSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "missing_clause_spices",  // Unique table name
            joinColumns = @JoinColumn(name = "missing_clause_id"),
            inverseJoinColumns = @JoinColumn(name = "spice_id")
    )
    @JsonIgnore
    private Set<SpicesAndSeasonings> spicesSet = new HashSet<>();

    private String deletedFlag = "N";

    @ManyToMany(mappedBy = "missingClauseRecipes")
    @JsonIgnore
    private Set<OrderedRecipe> recipes = new HashSet<>();


}
