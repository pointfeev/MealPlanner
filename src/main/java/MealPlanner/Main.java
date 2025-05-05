package MealPlanner;

import MealPlanner.GUI.MainFrame;
import MealPlanner.Models.FoodItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        System.exit(0);

        /*StringBuilder testString = new StringBuilder();
        try {
            testString.append("Connecting to the database...\n");
            if (!DatabaseHelper.connect()) {
                return;
            }
            testString.append("Setting up the database...\n");
            if (!DatabaseHelper.setup()) {
                return;
            }
            testString.append("Connected successfully, running a query...\n");
            ArrayList<HashMap<String, Object>> results;
            try {
                results = DatabaseHelper.executeQuery("SELECT table_name FROM all_tables WHERE owner = SYS_CONTEXT('USERENV', 'CURRENT_USER')");
            } catch (SQLException exception) {
                testString.append("Query failed!\n");
                return;
            }
            for (HashMap<String, Object> result : results) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    testString.append("%s: %s\n".formatted(entry.getKey(), entry.getValue()));
                }
            }
            testString.append("Query successful!\n");
        } finally {
            DatabaseHelper.disconnect();
            testString.append("Disconnected from the database.\n");
            displayInfoDialog(testString.toString());
            System.exit(0);
        }*/
    }
}