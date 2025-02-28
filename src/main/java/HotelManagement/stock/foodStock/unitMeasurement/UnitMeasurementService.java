package HotelManagement.stock.foodStock.unitMeasurement;

import HotelManagement.jwt.TokenRefreshRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
public UnitMeasurement updateUnitmeasurement(Long id , UnitMeasurementDto unitMeasurementDto){
        UnitMeasurement existingUnitMeasurement = unitMeasurementRepo.findById(id).get();
        existingUnitMeasurement.setUnit(unitMeasurementDto.getUnit());
        existingUnitMeasurement.setSubUnit(unitMeasurementDto.getSubUnit());
        existingUnitMeasurement.setUnitMeasurementNumber(existingUnitMeasurement.getUnitMeasurementNumber());
        existingUnitMeasurement.setId(existingUnitMeasurement.getId());
        existingUnitMeasurement.setUnitName(unitMeasurementDto.getUnitName());
        existingUnitMeasurement.setSubUnitName(unitMeasurementDto.getSubUnitName());
        unitMeasurementRepo.save(existingUnitMeasurement);
        return existingUnitMeasurement;
}

public List<UnitMeasurement> getAllUnits(){
        return unitMeasurementRepo.findAll();
}

public Optional<UnitMeasurement> findUnitById(Long id){
        return unitMeasurementRepo.findById(id);
}
public void  deleteUni(Long id){
        Optional<UnitMeasurement>optionalUnitMeasurement = unitMeasurementRepo.findById(id);
        if(optionalUnitMeasurement.isPresent()){
            UnitMeasurement unitMeasurement = optionalUnitMeasurement.get();
            unitMeasurement.setDeleted(true);
            unitMeasurementRepo.save(unitMeasurement);
        }
}

}
