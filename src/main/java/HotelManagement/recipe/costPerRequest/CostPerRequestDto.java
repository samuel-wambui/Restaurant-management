package HotelManagement.recipe.costPerRequest;

import lombok.Data;

@Data
public class CostPerRequestDto {
    private String recipeNumber;
    private String stockName;
    private Double foodStockQuantity;
    private String spiceNumber;
}
