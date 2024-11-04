package HotelManagement.costing;

import HotelManagement.ingredients.Ingredients;
import HotelManagement.spices.SpicesAndSeasonings;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
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

    private LocalDateTime localDateTime;

    // Mapping to either Ingredient or Spice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commodity_id", insertable = false, updatable = false)
    private Ingredients ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commodity_id", insertable = false, updatable = false)
    private SpicesAndSeasonings spice;


    private String deletedFlag = "N";




}

