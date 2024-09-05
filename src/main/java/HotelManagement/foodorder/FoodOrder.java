package HotelManagement.foodorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "food_order")
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "food_order_id"))
    private List<FoodOrderItem> orderItems;

    private Double totalPrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date orderTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
