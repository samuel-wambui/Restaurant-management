package HotelManagement.costing;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Costing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double quantity;
    private Double unitPrice;
    private Double totalCost;
    private Double discount;

    @Enumerated(EnumType.STRING)
    private CostCategory costCategory;
    private Long commodityId;
    private String stockNumber;

    private String deletedFlag = "N";

    private Long recipeId;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime date;


}
