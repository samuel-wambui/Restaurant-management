package HotelManagement.costing;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostingDto {
    private String quantity;
    private Double unitCost;
    private Double totalCost;
    private Double discount;
    private CostCategory costCategory;
    private Long commodityId;
    private String foodSockNumber;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime date;

}

