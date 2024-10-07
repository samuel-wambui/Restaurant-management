package HotelManagement.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StockService {
    @Autowired
    StockEntityRepo stockEntityRepo;
    @Autowired
    ProfitMultiplierRepo profitMultiplierRepo;
    public StockEntity addStock(StockDto stockDto){
        StockEntity stock = new StockEntity();
        stock.setName(stockDto.getStockName());
        stock.setQuantity(stockDto.getQuantity());
        stock.setCost(stockDto.getCost());
   Optional<ProfitMultiplier> optionalProfitMultiplier = profitMultiplierRepo.findByStockName(stockDto.getStockName());
        if (optionalProfitMultiplier.isPresent()) {
            ProfitMultiplier profitMultiplier = optionalProfitMultiplier.get();

            stock.setExpectedProfit((profitMultiplier.getMultiplier()*stockDto.getCost()));

        }

        return stockEntityRepo.save(stock);
    }

}
