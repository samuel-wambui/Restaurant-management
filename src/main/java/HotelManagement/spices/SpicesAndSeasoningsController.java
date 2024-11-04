package HotelManagement.spices;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spices")
public class SpicesAndSeasoningsController {

    @Autowired
    private SpicesAndSeasoningsService spicesService;

    // Create
    @PostMapping
    public ResponseEntity<SpicesAndSeasonings> createSpice(@RequestBody SpicesAndSeasoningsDto spiceDto) {
        SpicesAndSeasonings createdSpice = spicesService.createSpice(spiceDto);
        return new ResponseEntity<>(createdSpice, HttpStatus.CREATED);
    }

    // Read All
    @GetMapping
    public ResponseEntity<List<SpicesAndSeasonings>> getAllSpices() {
        List<SpicesAndSeasonings> spices = spicesService.getAllSpices();
        return new ResponseEntity<>(spices, HttpStatus.OK);
    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<SpicesAndSeasonings> getSpiceById(@PathVariable Long id) {
        SpicesAndSeasonings spice = spicesService.getSpiceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spice not found with id: " + id));
        return new ResponseEntity<>(spice, HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<SpicesAndSeasonings> updateSpice(@PathVariable Long id, @RequestBody SpicesAndSeasoningsDto spiceDto) {
        SpicesAndSeasonings updatedSpice = spicesService.updateSpice(id, spiceDto);
        return new ResponseEntity<>(updatedSpice, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpice(@PathVariable Long id) {
        spicesService.deleteSpice(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

