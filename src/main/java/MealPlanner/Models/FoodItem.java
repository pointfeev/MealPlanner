package MealPlanner.Models;

import MealPlanner.GUI.Details.DetailsDialog;
import MealPlanner.Models.Annotations.CheckString;
import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.PrimaryKey;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FoodItem extends Model {
    @Ignore public static final String TABLE = "food_item";

    @PrimaryKey public Number id;
    @NotNull public String name;
    @CheckString({"fruit", "vegetable", "grains", "protein", "dairy"}) public String food_group;
    @NotNull public String unit;
    public Number calories;
    public Number fat;
    public Number cholesterol;
    public Number sodium;
    public Number carbohydrates;
    public Number dietary_fiber;
    public Number sugars;
    public Number protein;

    public static String formatMilligrams(int value) {
        if (value >= 1000) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            double grams = value / 1000.0;
            String formattedGrams = decimalFormat.format(grams).replaceAll("\\.?0*$", "");
            return String.format("%s %s", formattedGrams, grams == 1 ? "gram" : "grams");
        }
        return String.format("%d %s", value, value == 1 ? "milligram" : "milligrams");
    }

    public void getDetails(String labelText, Number quantity) {
        if (quantity == null) {
            quantity = 1;
        }

        ArrayList<String> keysList = new ArrayList<>();
        ArrayList<String> valuesList = new ArrayList<>();

        if (food_group != null) {
            keysList.add("Food Group");
            valuesList.add(food_group);
        }
        if (calories != null) {
            keysList.add("Calories");
            valuesList.add(String.valueOf(calories.intValue() * quantity.intValue()));
        }
        if (fat != null) {
            keysList.add("Fat");
            valuesList.add(formatMilligrams(fat.intValue() * quantity.intValue()));
        }
        if (cholesterol != null) {
            keysList.add("Cholesterol");
            valuesList.add(formatMilligrams(cholesterol.intValue() * quantity.intValue()));
        }
        if (sodium != null) {
            keysList.add("Sodium");
            valuesList.add(formatMilligrams(sodium.intValue() * quantity.intValue()));
        }
        if (carbohydrates != null) {
            keysList.add("Carbohydrates");
            valuesList.add(formatMilligrams(carbohydrates.intValue() * quantity.intValue()));
        }
        if (dietary_fiber != null) {
            keysList.add("Dietary Fiber");
            valuesList.add(formatMilligrams(dietary_fiber.intValue() * quantity.intValue()));
        }
        if (sugars != null) {
            keysList.add("Sugars");
            valuesList.add(formatMilligrams(sugars.intValue() * quantity.intValue()));
        }
        if (protein != null) {
            keysList.add("Protein");
            valuesList.add(formatMilligrams(protein.intValue() * quantity.intValue()));
        }

        new DetailsDialog(labelText, keysList.toArray(new String[0]), valuesList.toArray(new String[0]));
    }

    public void getDetails(String labelText) {
        getDetails(labelText, null);
    }

    public void getDetails() {
        getDetails(toString());
    }

    public String formatQuantity(int quantity) {
        return "%s %s(s) of %s".formatted(quantity, unit, name);
    }

    @Override
    public String toString() {
        return "%s of %s".formatted(unit.substring(0, 1).toUpperCase() + unit.substring(1).toLowerCase(), name);
    }
}
