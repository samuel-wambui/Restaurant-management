package HotelManagement.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryDto {
    private String itemName;
    private int quantity;
    private String category;
    private String supplier;
    private String unit;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate recordedDate;
}
