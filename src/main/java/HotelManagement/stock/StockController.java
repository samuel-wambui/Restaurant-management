package HotelManagement.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;


    @PostMapping("/add")
    public ResponseEntity<StockEntity> addStock(@RequestBody StockDto stockDto) {
        StockEntity addedStock = stockService.addStock(stockDto);
        return ResponseEntity.ok(addedStock);
    }
}
