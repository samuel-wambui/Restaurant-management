package HotelManagement.housekeeping;

import lombok.Data;

@Data
public class HousekeepingDto {
    private Long roomId;
    private Boolean isCleaned;
    private String lastCleanedDate;
}
