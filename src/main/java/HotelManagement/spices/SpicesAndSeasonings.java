package HotelManagement.spices;

import HotelManagement.costing.Costing;
import HotelManagement.recipe.Recipe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SpicesAndSeasonings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    private String deletedFlag = "N";

    @OneToMany(mappedBy = "spice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Costing> costings = new ArrayList<>();
}



