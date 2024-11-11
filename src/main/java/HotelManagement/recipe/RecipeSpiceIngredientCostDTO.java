package HotelManagement.recipe;

import lombok.Data;
import java.util.*;
import java.util.stream.Collectors;


@Data
public class RecipeSpiceIngredientCostDTO {

    private Long recipeId;
    private String recipeName;
    private List<IngredientSpiceCostDTO> ingredients;
    private List<IngredientSpiceCostDTO> spices;

    public RecipeSpiceIngredientCostDTO(Long recipeId, String recipeName, List<IngredientSpiceCostDTO> ingredients, List<IngredientSpiceCostDTO> spices) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.spices = spices;
    }

    public Double getTotalIngredientCost() {
        return ingredients.stream()
                .map(IngredientSpiceCostDTO::getCost)
                .filter(Objects::nonNull)
                .reduce(Double::sum)
                .orElse(null);
    }

    public Double getTotalSpiceCost() {
        return spices.stream()
                .map(IngredientSpiceCostDTO::getCost)
                .filter(Objects::nonNull)
                .reduce(Double::sum)
                .orElse(null);
    }

    public Double getRecipeTotalCost() {
        Double totalIngredientCost = getTotalIngredientCost();
        Double totalSpiceCost = getTotalSpiceCost();
        if (totalIngredientCost == null || totalSpiceCost == null) {
            return null;
        }
        return totalIngredientCost + totalSpiceCost;
    }

    @Data
    public static class IngredientSpiceCostDTO {
        private String name;
        private Double cost;
        private String quantity;

        public IngredientSpiceCostDTO(String name, Double cost, String quantity) {
            this.name = name;
            this.cost = cost;
            this.quantity = quantity;
        }
    }

    private static List<IngredientSpiceCostDTO> parseIngredientsOrSpices(String names, String costs, String quantities) {
        List<IngredientSpiceCostDTO> items = new ArrayList<>();
        if (names != null) {
            String[] nameArray = names.split(",");
            String[] costArray = (costs != null) ? costs.split(",") : new String[nameArray.length];
            String[] quantityArray = (quantities != null) ? quantities.split(",") : new String[nameArray.length];

            int length = nameArray.length;
            for (int i = 0; i < length; i++) {
                String name = nameArray[i].trim();

                // Check if quantityArray[i] is not null before trimming
                String quantity = (i < quantityArray.length && quantityArray[i] != null) ? quantityArray[i].trim() : null;

                Double cost = null;
                if (i < costArray.length && costArray[i] != null) {
                    try {
                        cost = Double.parseDouble(costArray[i].trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid cost format for ingredient/spice: " + costArray[i]);
                    }
                }

                items.add(new IngredientSpiceCostDTO(name, cost, quantity));
            }
        }
        return items;
    }

    public static RecipeSpiceIngredientCostDTO fromProjection(RecipeSpiceIngredientCostProjection projection) {
        List<IngredientSpiceCostDTO> ingredients = parseIngredientsOrSpices(
                projection.getIngredientNames(),
                projection.getIndividualIngredientCosts(),
                projection.getIngredientQuantities()
        );

        List<IngredientSpiceCostDTO> spices = parseIngredientsOrSpices(
                projection.getSpiceNames(),
                projection.getIndividualSpiceCosts(),
                projection.getSpiceQuantities()
        );

        return new RecipeSpiceIngredientCostDTO(
                projection.getRecipeId(),
                projection.getRecipeName(),
                ingredients,
                spices
        );
    }

    public static List<RecipeSpiceIngredientCostDTO> fromProjections(List<RecipeSpiceIngredientCostProjection> projections) {
        return projections.stream()
                .collect(Collectors.groupingBy(RecipeSpiceIngredientCostProjection::getRecipeId))
                .entrySet().stream()
                .map(entry -> {
                    RecipeSpiceIngredientCostProjection first = entry.getValue().get(0);
                    List<IngredientSpiceCostDTO> ingredients = parseIngredientsOrSpices(
                            first.getIngredientNames(),
                            first.getIndividualIngredientCosts(),
                            first.getIngredientQuantities()
                    );

                    List<IngredientSpiceCostDTO> spices = parseIngredientsOrSpices(
                            first.getSpiceNames(),
                            first.getIndividualSpiceCosts(),
                            first.getSpiceQuantities()
                    );

                    return new RecipeSpiceIngredientCostDTO(
                            first.getRecipeId(),
                            first.getRecipeName(),
                            ingredients,
                            spices
                    );
                })
                .collect(Collectors.toList());
    }
}
