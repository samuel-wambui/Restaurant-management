package HotelManagement.stock;

import lombok.Data;

@Data
public class StockDto {
    private String stockName;
    private String quantity;
    private Double cost;
}
