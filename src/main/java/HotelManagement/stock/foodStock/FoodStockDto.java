package HotelManagement.stock.foodStock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class FoodStockDto {
    private String name;
    private String unitNumber;
    private String foodStockType;
    private Set<Long> categoryIds;
    @JsonFormat(pattern = "M/d/yyyy, HH:mm:ss")
    private LocalDateTime purchaseDate;
    @JsonFormat(pattern = "M/d/yyyy, HH:mm:ss")
    private LocalDateTime expiryDate;
}
