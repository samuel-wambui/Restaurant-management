package HotelManagement.meals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDto {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
}
