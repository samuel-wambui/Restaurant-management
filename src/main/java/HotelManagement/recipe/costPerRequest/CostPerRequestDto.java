package HotelManagement.recipe.costPerRequest;

import lombok.Data;

import java.util.List;

@Data
public class CostPerRequestDto {
    private String recipeNumber;
    private List<FoodStockRequestDto> foodStocks;
    private String spiceNumber;

}

