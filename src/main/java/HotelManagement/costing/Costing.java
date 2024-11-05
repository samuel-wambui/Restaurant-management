package HotelManagement.costing;

import HotelManagement.ingredients.Ingredients;
import HotelManagement.spices.SpicesAndSeasonings;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Costing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quantity;
    private Double cost;

    @Enumerated(EnumType.STRING)
    private CostCategory costCategory;

    private String deletedFlag = "N";

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", unique = true)  // unique constraint to enforce one-to-one
    private Ingredients ingredient;

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "spices_and_seasonings_id")
    private SpicesAndSeasonings spicesAndSeasonings;

    // Optionally override equals() and hashCode() if necessary
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Costing costing = (Costing) o;
        return id != null && id.equals(costing.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
