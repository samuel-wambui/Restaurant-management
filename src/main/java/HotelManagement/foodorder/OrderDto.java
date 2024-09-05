package HotelManagement.foodorder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String customerName;
    private List<FoodOrderItemDto> orderItems;

}
