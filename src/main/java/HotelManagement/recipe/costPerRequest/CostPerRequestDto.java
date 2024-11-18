package HotelManagement.recipe.costPerRequest;

import lombok.Data;

@Data
public class CostPerRequestDto {
    private String recipeNumber;
    private String foodStockNumber;
    private Double foodStockQuantity;
    private String spiceNumber;
}
