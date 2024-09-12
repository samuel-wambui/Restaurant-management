package HotelManagement.meals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    public List<MealEntity> getAllMeals() {
        return mealRepository.findAll();
    }

    public MealEntity getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
    }

    public MealEntity getLatestMeal() {
        return mealRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("No meals found"));
    }

    public MealEntity createMeal(MealDto mealDto) {
        MealEntity meal = new MealEntity();
        meal.setName(mealDto.getName());
        meal.setDescription(mealDto.getDescription());
        meal.setPrice(mealDto.getPrice());
        // Remove image URL setting since we are not handling images
        return mealRepository.save(meal);
    }

    public MealEntity updateMeal(Long id, MealDto mealDto) {
        MealEntity existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        existingMeal.setName(mealDto.getName());
        existingMeal.setDescription(mealDto.getDescription());
        existingMeal.setPrice(mealDto.getPrice());
        // Remove image URL update since we are not handling images
        return mealRepository.save(existingMeal);
    }

    public void deleteMeal(Long id) {
        if (!mealRepository.existsById(id)) {
            throw new RuntimeException("Meal not found");
        }
        mealRepository.deleteById(id);
    }
}
