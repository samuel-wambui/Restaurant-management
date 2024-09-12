package HotelManagement.foodorder;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FoodOrderItem {
    private String itemName;
    private Double price;

    // Ensure this constructor exists if you’re using it
    public FoodOrderItem(String itemName, Double price) {
        this.itemName = itemName;
        this.price = price;
    }

    // No-argument constructor for JPA
    public FoodOrderItem() {
    }
}

