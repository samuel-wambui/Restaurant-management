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
    private Double totalIngredientCost;
    private Double totalSpiceCost;
    private Double recipeTotalCost; // New field to store total recipe cost

    public RecipeSpiceIngredientCostDTO(Long recipeId, String recipeName, List<IngredientSpiceCostDTO> ingredients,
                                        List<IngredientSpiceCostDTO> spices, Double totalIngredientCost, Double totalSpiceCost, Double recipeTotalCost) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.spices = spices;
        this.totalIngredientCost = totalIngredientCost;
        this.totalSpiceCost = totalSpiceCost;
        this.recipeTotalCost = recipeTotalCost;
    }

    @Data
    public static class IngredientSpiceCostDTO {
        private String name;
        private Double cost;
        private String quantity; // New field for quantity

        public IngredientSpiceCostDTO(String name, Double cost, String quantity) {
            this.name = name;
            this.cost = cost;
            this.quantity = quantity;
        }
    }

    // Updated method to parse names, costs, and quantities
    private static List<IngredientSpiceCostDTO> parseIngredientsOrSpices(String names, String costs, String quantities) {
        List<IngredientSpiceCostDTO> items = new ArrayList<>();
        if (names != null && costs != null && quantities != null) {
            String[] nameArray = names.split(",");
            String[] costArray = costs.split(",");
            String[] quantityArray = quantities.split(",");

            int length = Math.min(Math.min(nameArray.length, costArray.length), quantityArray.length);
            for (int i = 0; i < length; i++) {
                String name = nameArray[i].trim();
                String quantity = quantityArray[i].trim();
                try {
                    Double cost = Double.parseDouble(costArray[i].trim());
                    items.add(new IngredientSpiceCostDTO(name, cost, quantity));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid cost format for ingredient/spice: " + costArray[i]);
                }
            }
        }
        return items;
    }

    // Updated method to map individual projection to DTO
    public static RecipeSpiceIngredientCostDTO fromProjection(RecipeSpiceIngredientCostProjection projection) {
        List<IngredientSpiceCostDTO> ingredients = parseIngredientsOrSpices(
                projection.getIngredientNames(),
                projection.getIndividualIngredientCosts(),
                projection.getIngredientQuantities() // New quantity field
        );

        List<IngredientSpiceCostDTO> spices = parseIngredientsOrSpices(
                projection.getSpiceNames(),
                projection.getIndividualSpiceCosts(),
                projection.getSpiceQuantities() // New quantity field
        );

        return new RecipeSpiceIngredientCostDTO(
                projection.getRecipeId(),
                projection.getRecipeName(),
                ingredients,
                spices,
                projection.getTotalIngredientCost(),
                projection.getTotalSpiceCost(),
                projection.getRecipeTotalCost() // Total recipe cost from projection
        );
    }

    // Updated method to map list of projections to a list of DTOs
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

                    Double totalIngredientCost = entry.getValue().stream().mapToDouble(RecipeSpiceIngredientCostProjection::getTotalIngredientCost).sum();
                    Double totalSpiceCost = entry.getValue().stream().mapToDouble(RecipeSpiceIngredientCostProjection::getTotalSpiceCost).sum();
                    Double recipeTotalCost = entry.getValue().stream().mapToDouble(RecipeSpiceIngredientCostProjection::getRecipeTotalCost).sum();

                    return new RecipeSpiceIngredientCostDTO(
                            first.getRecipeId(),
                            first.getRecipeName(),
                            ingredients,
                            spices,
                            totalIngredientCost,
                            totalSpiceCost,
                            recipeTotalCost // Calculate recipe total cost
                    );
                })
                .collect(Collectors.toList());
    }
}
