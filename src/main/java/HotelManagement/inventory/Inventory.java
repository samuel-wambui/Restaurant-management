package HotelManagement.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private int quantity;

    private String category;

    private String supplier;

    private String unit;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate recordedDate;
}
