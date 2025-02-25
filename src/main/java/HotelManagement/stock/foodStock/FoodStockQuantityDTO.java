package HotelManagement.stock.foodStock;

import lombok.Data;

@Data
public class FoodStockQuantityDTO {
    private String name;
    private Double quantity;

    public FoodStockQuantityDTO(String name, Double quantity) {
        this.name = name;
        this.quantity = quantity;
    }}
