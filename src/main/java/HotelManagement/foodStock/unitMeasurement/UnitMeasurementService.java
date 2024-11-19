package HotelManagement.foodStock.unitMeasurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnitMeasurementService {
    @Autowired
    UnitMeasurementRepo unitMeasurementRepo;
    
    public UnitMeasurement createUnitMeasurement(UnitMeasurementDto unitMeasurementDto){
        UnitMeasurement unitMeasurement =   new UnitMeasurement();
        unitMeasurement.setUnitName(unitMeasurementDto.getUnitName());
        unitMeasurement.setUnit(unitMeasurementDto.getUnit());
        unitMeasurement.setSubUnitName(unitMeasurementDto.getSubUnitName());
        unitMeasurement.setSubUnit(unitMeasurementDto.getSubUnit());
        unitMeasurement.setUnitMeasurementNumber(generateUnitMeasurementNumber());
        return unitMeasurementRepo.save(unitMeasurement);
    }

    private String generateUnitMeasurementNumber() {
        Integer lastNumber = unitMeasurementRepo.findLastServiceNumber();
        int nextNumber = (lastNumber != null) ? lastNumber + 1 : 1;
        String formattedNumber = String.format("%03d", nextNumber);
        return "U" + formattedNumber;

    }
}
