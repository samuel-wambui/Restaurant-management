package HotelManagement.costing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostingDto {
    private String quantity;
    private Double cost;
    private CostCategory costCategory;
    private Long commodityId;
    private Long recipeId;
}

