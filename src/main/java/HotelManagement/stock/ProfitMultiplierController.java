package HotelManagement.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/multiplier")
public class ProfitMultiplierController {

    @Autowired
    private ProfitMultiplierService profitMultiplierService;


    @PostMapping("/add")
    public ResponseEntity<ProfitMultiplier> addOrUpdateMultiplier(@RequestBody MultiplierDto profitMultiplier) {
        ProfitMultiplier savedMultiplier = profitMultiplierService.addOrUpdateMultiplier(profitMultiplier);
        return ResponseEntity.ok(savedMultiplier);
    }


    @GetMapping("/all")
    public ResponseEntity<List<ProfitMultiplier>> getAllMultipliers() {
        List<ProfitMultiplier> multipliers = profitMultiplierService.getAllMultipliers();
        return ResponseEntity.ok(multipliers);
    }


    @GetMapping("/getByName")
    public ResponseEntity<ProfitMultiplier> getMultiplierByStockName(@RequestParam String stockName) {
        Optional<ProfitMultiplier> multiplier = profitMultiplierService.getMultiplierByStockName(stockName);
        return multiplier.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMultiplier(@PathVariable Long id) {
        profitMultiplierService.deleteMultiplier(id);
        return ResponseEntity.ok().build();
    }
}
