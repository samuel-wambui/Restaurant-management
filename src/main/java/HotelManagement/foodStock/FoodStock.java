package HotelManagement.foodStock;

import HotelManagement.recipe.Recipe;
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

    @ManyToMany(mappedBy = "ingredientsSet")
    @JsonIgnore // Prevent recursion with Recipe
    private Set<Recipe> recipes = new HashSet<>();

    private String deletedFlag = "N";


}
