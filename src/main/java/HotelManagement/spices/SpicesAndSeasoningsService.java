package HotelManagement.spices;



import HotelManagement.recipe.Recipe;
import HotelManagement.recipe.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpicesAndSeasoningsService {

    @Autowired
    private SpicesAndSeasoningsRepo spicesRepo;
    @Autowired
    private RecipeRepo recipeRepo;

    // Create
    public SpicesAndSeasonings createSpice(SpicesAndSeasoningsDto spiceDto) {


            // Create a new SpicesAndSeasonings instance
            SpicesAndSeasonings spice = new SpicesAndSeasonings();
            spice.setName(spiceDto.getName());


            return spicesRepo.save(spice);

    }


    // Read
    public List<SpicesAndSeasonings> getAllSpices() {
        return spicesRepo.findAll();
    }

    public Optional<SpicesAndSeasonings> getSpiceById(Long id) {
        return spicesRepo.findById(id);
    }

    // Update
    public SpicesAndSeasonings updateSpice(Long id, SpicesAndSeasoningsDto spiceDto) {
        SpicesAndSeasonings spice = spicesRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spice not found with id: " + id));
        spice.setName(spiceDto.getName());
        return spicesRepo.save(spice);
    }

    // Delete
    public void deleteSpice(Long id) {
        spicesRepo.deleteById(id);
    }
}

