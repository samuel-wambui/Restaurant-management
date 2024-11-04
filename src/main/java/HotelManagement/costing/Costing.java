package HotelManagement.costing;

import HotelManagement.ingredients.Ingredients;
import HotelManagement.spices.SpicesAndSeasonings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Costing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quantity;
    private Double cost;

    @Enumerated(EnumType.STRING)
    private CostCategory costCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;

    private String deletedFlag = "N";
}
