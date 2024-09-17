package HotelManagement.tables;
import HotelManagement.billing.Billing;
import HotelManagement.rooms.booking.Booking;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity

public class TablesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY
    )

    private  long id;
    private String tableNumber;;
    private int seatingCapacity;

    @OneToMany(mappedBy = "table")
    private Set<Billing> billing;

}
