package HotelManagement.rooms.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Data
public class BookingDTO {
    private Long roomId;
    private String guestName;
    private String guestEmail;
    private String guestPhoneNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;


    private double amountPaid;  // New field for amount paid
    private PaymentMethod paymentMethod;
}
