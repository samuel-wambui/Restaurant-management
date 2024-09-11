package HotelManagement.service.servicebooking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBookingDto {
    private Long customerId;
    private Long serviceId;
    private String bookingDate;
    private String timeSlot;
}
