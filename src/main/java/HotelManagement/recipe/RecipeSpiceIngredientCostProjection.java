package HotelManagement.recipe;

public interface RecipeSpiceIngredientCostProjection {

     // Basic recipe information
     Long getRecipeId();
     String getRecipeName();

     // Ingredient details
     String getIngredientNames();              // Concatenated ingredient names
     String getIndividualIngredientCosts();    // Concatenated individual ingredient costs
     String getIngredientQuantities();         // Concatenated ingredient quantities (new field)
     Double getTotalIngredientCost();          // Total cost of all ingredients

     // Spice details
     String getSpiceNames();                   // Concatenated spice names
     String getIndividualSpiceCosts();         // Concatenated individual spice costs
     String getSpiceQuantities();              // Concatenated spice quantities (new field)
     Double getTotalSpiceCost();               // Total cost of all spices

     // Total recipe cost (sum of ingredient and spice costs)
     Double getRecipeTotalCost();              // Total recipe cost (new field)
}
