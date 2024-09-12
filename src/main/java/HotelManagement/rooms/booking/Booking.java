package HotelManagement.rooms.booking;

import HotelManagement.rooms.Rooms;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Rooms room;

    private String guestName;
    private String guestEmail;
    private String guestPhoneNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;


    private boolean isPaid;
    private double amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
