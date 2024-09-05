package HotelManagement.foodorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrderItemDto {
    private String itemName;
    private Double price;
}
