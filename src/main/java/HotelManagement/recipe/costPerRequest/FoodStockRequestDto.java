package HotelManagement.recipe.costPerRequest;

import lombok.Data;

@Data
public class FoodStockRequestDto {
    private Long id;
    private Double foodStockQuantity;
    private MeasurementCategory measurementCategory;
}
