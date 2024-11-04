package HotelManagement.spices;

import HotelManagement.costing.Costing;
import HotelManagement.recipe.Recipe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SpicesAndSeasonings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "spicesSet")
    private Set<Recipe> recipes = new HashSet<>();

    private String deletedFlag = "N";

    @OneToMany(mappedBy = "spice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Costing> costings = new ArrayList<>();
}
