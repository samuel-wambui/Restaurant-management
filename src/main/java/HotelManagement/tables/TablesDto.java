package HotelManagement.tables;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class TablesDto {
    private String tableNumber;
    private int seatingCapacity;
}
