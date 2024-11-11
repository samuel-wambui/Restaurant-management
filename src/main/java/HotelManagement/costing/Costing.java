package HotelManagement.costing;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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
    private Long commodityId;

    private String deletedFlag = "N";

    private Long recipeId;


}
