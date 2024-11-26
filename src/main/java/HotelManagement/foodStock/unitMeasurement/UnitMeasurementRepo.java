package HotelManagement.foodStock.unitMeasurement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMeasurementRepo extends JpaRepository<UnitMeasurement, Long> {
    @Query(value = "SELECT MAX(CAST(SUBSTRING(unit_measurement_number, 4) AS UNSIGNED)) " +
            "FROM unit_measurement", nativeQuery = true)
    Integer findLastServiceNumber();

    UnitMeasurement findByUnitMeasurementNumber(String unitNumber);
}
