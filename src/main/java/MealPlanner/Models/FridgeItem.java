package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.OrderBy;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FridgeItem extends Model {
    @Ignore public static final String TABLE = "fridge_item";

    @PrimaryKey public Number id;
    @NotNull @OrderBy("ASC") public Number food_id;
    @NotNull @OrderBy("DESC") public Number quantity;

    @Ignore private FoodItem foodItem;

    public FoodItem getFoodItem() {
        if (foodItem == null) {
            FoodItem foodItemCriteria = new FoodItem();
            foodItemCriteria.id = food_id;
            FoodItem[] foodItems = foodItemCriteria.select();
            if (foodItems == null || foodItems.length == 0) {
                throw new RuntimeException("Fridge item does not have a food item associated with it! Fridge ID: %s, Food ID: %s".formatted(id, food_id));
            }
            foodItem = foodItems[0];
        }
        return foodItem;
    }
}
