package MealPlanner;

import MealPlanner.GUI.MainFrame;
import MealPlanner.Models.FoodItem;

import static MealPlanner.GUI.MainFrame.displayInfoDialog;

public class Main {
    public static void main(String[] args) {
        MainFrame.get();
        // TODO: move and remove the rest of this function once the UI is set up

        DatabaseHelper.connect();
        DatabaseHelper.setup();

        // testing selection
        FoodItem foodItemCriteria = new FoodItem();
        FoodItem[] foodItems = foodItemCriteria.select();
        if (foodItems != null) {
            StringBuilder foodItemBuilder = new StringBuilder();
            for (FoodItem foodItem : foodItems) {
                foodItemBuilder.append("ID: %s, Name: %s, Unit: %s, Group: %s\n".formatted(
                        foodItem.id,
                        foodItem.name,
                        foodItem.unit,
                        foodItem.food_group));
            }
            displayInfoDialog(foodItemBuilder.toString());
        }

        // testing WHERE selection
        foodItemCriteria = new FoodItem();
        foodItemCriteria.name = "White Sandwich Bread";
        foodItems = foodItemCriteria.select();
        if (foodItems != null) {
            StringBuilder foodItemBuilder = new StringBuilder();
            for (FoodItem foodItem : foodItems) {
                foodItemBuilder.append("ID: %s, Name: %s, Unit: %s, Group: %s\n".formatted(
                        foodItem.id,
                        foodItem.name,
                        foodItem.unit,
                        foodItem.food_group));
            }
            displayInfoDialog(foodItemBuilder.toString());
        }

        // testing WHERE LIKE selection
        foodItemCriteria.name = null; // remove name from search
        foodItemCriteria.unit = "%spoon";
        foodItems = foodItemCriteria.select();
        if (foodItems != null) {
            StringBuilder foodItemBuilder = new StringBuilder();
            for (FoodItem foodItem : foodItems) {
                foodItemBuilder.append("ID: %s, Name: %s, Unit: %s, Group: %s\n".formatted(
                        foodItem.id,
                        foodItem.name,
                        foodItem.unit,
                        foodItem.food_group));
            }
            displayInfoDialog(foodItemBuilder.toString());
        }

        // testing insert
        FoodItem foodItemTest = new FoodItem();
        foodItemTest.name = "White Sandwich Bread";
        foodItemTest.unit = "slice";
        if (foodItemTest.insert()) {
            displayInfoDialog("inserted %s", foodItemTest.id);
        }

        // testing update
        foodItemTest.food_group = "grains";
        if (foodItemTest.update()) {
            displayInfoDialog("updated %s", foodItemTest.id);
        }

        // testing delete
        if (foodItemTest.delete()) {
            displayInfoDialog("deleted %s", foodItemTest.id);
        }

        DatabaseHelper.disconnect();
        System.exit(0);
    }
}