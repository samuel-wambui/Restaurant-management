package HotelManagement.foodStock;

import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.missingClause.MissingClauseRecipe;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
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

    private String stockName;
    private String unitNumber;

    @ManyToMany(mappedBy = "foodStockSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<Recipe> recipes = new HashSet<>();

    @ManyToMany(mappedBy = "foodStockSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<MissingClauseRecipe> missingClauseRecipes = new HashSet<>();

    private String deletedFlag = "N";
    private String depletedFlag= "N";
    private String stockNumber;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime purchaseDate;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime expiryDate;
    private boolean expired= false;



}
