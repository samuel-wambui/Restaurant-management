package HotelManagement.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    @Autowired
    StockEntityRepo stockEntityRepo;
    @Autowired
    ProfitMultiplierRepo profitMultiplierRepo;
    public Stock addStock(StockDto stockDto) {
        Stock stock = new Stock();
        stock.setName(stockDto.getStockName());
        stock.setQuantity(stockDto.getQuantity());
        stock.setCost(stockDto.getCost());

        double numericQuantity = extractQuantity(stockDto.getQuantity());


        Optional<ProfitMultiplier> optionalProfitMultiplier = profitMultiplierRepo.findByStockName(stockDto.getStockName());
        if (optionalProfitMultiplier.isPresent()) {
            ProfitMultiplier profitMultiplier = optionalProfitMultiplier.get();


            double cost = stockDto.getCost();
            double multiplier = profitMultiplier.getMultiplier();
            double expectedProfit = (multiplier * cost * numericQuantity) - (cost * numericQuantity);

            //stock.setExpectedProfit(expectedProfit);
        }

        return stockEntityRepo.save(stock);
    }
    private double extractQuantity(String quantity) {
        String numericPart = quantity.replaceAll("[^\\d.]", "");
        return Double.parseDouble(numericPart);
    }

    public List<Stock> getAllStocks() {
        return stockEntityRepo.findAll();
    }


    public Optional<Stock> getStockById(Long id) {
        return stockEntityRepo.findById(id);
    }


    public void deleteStock(Long id) {
        stockEntityRepo.deleteById(id);
    }
}
