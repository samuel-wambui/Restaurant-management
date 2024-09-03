package HotelManagement.rooms;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@Data
public class RoomsDTO {
    private String roomNumber;
    private String roomType;
    private int capacity;
    private double price;
    private boolean isAvailable;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availableDate;
}
