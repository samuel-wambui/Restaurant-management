package HotelManagement.stock.foodStock.unitMeasurement;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UnitMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String unitName;
    private int unit;
    private String subUnitName;
    private int subUnit;
    private String unitMeasurementNumber;
    private boolean isDeleted= false;

}
