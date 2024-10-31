package HotelManagement.costing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostingDto {
    private String quantity;
    private Double cost;
    private CostCategory costCategory;
    private Long commodityId; // Represents either Ingredient or Spice ID
}
