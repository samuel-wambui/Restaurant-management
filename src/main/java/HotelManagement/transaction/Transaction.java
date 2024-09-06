package HotelManagement.transaction;

import HotelManagement.paymentmethods.PaymentsMethods;
import HotelManagement.rooms.booking.Booking;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String transactionCode;
    private String transactionNumber;
    private Double transactionAmount;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentsMethods paymentMethod;

    private String transactionMode;
    private String resultCode;
    private String resultDesc;
    private String status;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private String datePaid;
}
