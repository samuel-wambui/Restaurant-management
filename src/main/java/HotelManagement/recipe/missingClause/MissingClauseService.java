package HotelManagement.recipe.missingClause;

import HotelManagement.recipe.todayRecipe.OrderedRecipe;
import HotelManagement.recipe.todayRecipe.OrderedRecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MissingClauseService {
    @Autowired
    MissingClauseRepo missingClauseRepo;

    @Autowired
    OrderedRecipeRepo orderedRecipeRepo;
    public MissingClauseRecipe createMissingClause(MissingClauseDto missingClauseDto){
        MissingClauseRecipe missingClauseRecipe = new MissingClauseRecipe();
        missingClauseRecipe.setMissingClauseName(missingClauseDto.getMissingClauseName());
        missingClauseRecipe.setFoodStockSet(missingClauseDto.getFoodStockSet());
        missingClauseRecipe.setSpicesSet(missingClauseDto.getSpicesSet());
        Optional<OrderedRecipe> optionalOrderedRecipe= orderedRecipeRepo.findById(missingClauseDto.getOrderedRecipe());
        if(optionalOrderedRecipe.isPresent()){
            OrderedRecipe orderedRecipe = optionalOrderedRecipe.get();
            missingClauseRecipe.setOrderedRecipe(orderedRecipe.getId());
        }

        return missingClauseRepo.save(missingClauseRecipe);


    }

}
