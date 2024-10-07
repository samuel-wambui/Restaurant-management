package HotelManagement.stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfitMultiplierService {

    @Autowired
    private ProfitMultiplierRepo profitMultiplierRepo;


    public ProfitMultiplier addOrUpdateMultiplier(MultiplierDto multiplierDto) {
        ProfitMultiplier profitMultiplier =new ProfitMultiplier();
        profitMultiplier.setMultiplier(multiplierDto.getMultiplier());
        profitMultiplier.setStockName(multiplierDto.getStockName());
        return profitMultiplierRepo.save(profitMultiplier);
    }


    public List<ProfitMultiplier> getAllMultipliers() {
        return profitMultiplierRepo.findAll();
    }

    public Optional<ProfitMultiplier> getMultiplierByStockName(String stockName) {
        return profitMultiplierRepo.findByStockName(stockName);
    }

    public void deleteMultiplier(Long id) {
        profitMultiplierRepo.deleteById(id);
    }
}



