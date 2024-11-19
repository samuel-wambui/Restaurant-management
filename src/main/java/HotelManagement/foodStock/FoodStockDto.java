package HotelManagement.foodStock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodStockDto {
    private String name;
    private String unitNumber;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime purchaseDate;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime expiryDate;

}
