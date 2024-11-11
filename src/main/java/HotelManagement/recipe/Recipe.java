package HotelManagement.recipe;

import HotelManagement.foodStock.FoodStock;
import HotelManagement.menu.Menu;
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

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @JsonIgnore // Ignore serialization to prevent recursion
    private Set<FoodStock> foodStockSet = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_spices",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "spice_id")
    )
    @JsonIgnore // Ignore serialization to prevent recursion
    private Set<SpicesAndSeasonings> spicesSet = new HashSet<>();

    private String deletedFlag = "N";

    @ManyToMany(mappedBy = "recipeSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<Menu> recipes = new HashSet<>();
}

