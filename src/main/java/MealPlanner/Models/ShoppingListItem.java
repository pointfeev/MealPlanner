package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;

public class ShoppingListItem extends Model {
    @Ignore public static final String TABLE = "shopping_list_item";

    public Number food_id;
    public Number quantity;

    @Ignore private FoodItem foodItem;

    public FoodItem getFoodItem() {
        if (foodItem == null) {
            FoodItem foodItemCriteria = new FoodItem();
            foodItemCriteria.id = food_id;
            FoodItem[] foodItems = foodItemCriteria.select();
            if (foodItems == null || foodItems.length == 0) {
                throw new RuntimeException("Shopping list item does not have a food item associated with it! Food ID: %s".formatted(food_id));
            }
            foodItem = foodItems[0];
        }
        return foodItem;
    }

    public void clearCache() {
        foodItem = null;
    }
}
