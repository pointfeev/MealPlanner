package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeIngredient extends Model {
    @Ignore public static final String TABLE = "recipe_ingredient";

    @PrimaryKey @NotNull public Number recipe_id;
    @PrimaryKey @NotNull public Number food_id;
    @NotNull public Number quantity;

    @Ignore private FoodItem foodItem;

    public FoodItem getFoodItem() {
        if (foodItem == null) {
            FoodItem foodItemCriteria = new FoodItem();
            foodItemCriteria.id = food_id;
            FoodItem[] foodItems = foodItemCriteria.select();
            if (foodItems == null || foodItems.length == 0) {
                throw new RuntimeException("Recipe ingredient does not have a food item associated with it! Recipe ID: %s, Food ID: %s".formatted(recipe_id, food_id));
            }
            foodItem = foodItems[0];
        }
        return foodItem;
    }
}
