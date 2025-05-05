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

        FoodItem foodItemTest = new FoodItem();
        foodItemTest.name = "White Sandwich Bread";
        foodItemTest.unit = "slice";
        foodItemTest.insert();
        displayInfoDialog("inserted %s", foodItemTest.id);

        foodItemTest.food_group = "grains";
        foodItemTest.update();
        displayInfoDialog("updated %s", foodItemTest.id);

        foodItemTest.delete();
        displayInfoDialog("deleted %s", foodItemTest.id);

        DatabaseHelper.disconnect();
        System.exit(0);
    }
}