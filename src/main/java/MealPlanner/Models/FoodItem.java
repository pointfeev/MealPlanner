package MealPlanner.Models;

import MealPlanner.GUI.Details.DetailsDialog;
import MealPlanner.Models.Annotations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FoodItem extends Model {
    @Ignore public static final String TABLE = "food_item";

    @PrimaryKey public Number id;
    @NotNull @OrderBy("ASC") public String name;
    @CheckString({"fruit", "vegetable", "grains", "protein", "dairy"}) public String food_group;
    @NotNull @OrderBy("ASC") public String unit;
    @CheckNumberMinimum(0) public Number calories;
    @CheckNumberMinimum(0) public Number fat;
    @CheckNumberMinimum(0) public Number cholesterol;
    @CheckNumberMinimum(0) public Number sodium;
    @CheckNumberMinimum(0) public Number carbohydrates;
    @CheckNumberMinimum(0) public Number dietary_fiber;
    @CheckNumberMinimum(0) public Number sugars;
    @CheckNumberMinimum(0) public Number protein;

    public static String formatDecimal(Number value) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(value.doubleValue()));
        return bigDecimal.stripTrailingZeros().toPlainString();
    }

    public static String formatMilligrams(Number value) {
        double milligrams = value.doubleValue();
        if (milligrams >= 1000) {
            double grams = milligrams / 1000.0;
            String formattedGrams = formatDecimal(grams);
            return "%s %s".formatted(formattedGrams, formattedGrams.equals("1") ? "gram" : "grams");
        }
        String formattedMilligrams = formatDecimal(milligrams);
        return "%s %s".formatted(formattedMilligrams, formattedMilligrams.equals("1") ? "milligram" : "milligrams");
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
            valuesList.add(formatDecimal(calories.doubleValue() * quantity.doubleValue()));
        }
        if (fat != null) {
            keysList.add("Fat");
            valuesList.add(formatMilligrams(fat.doubleValue() * quantity.doubleValue()));
        }
        if (cholesterol != null) {
            keysList.add("Cholesterol");
            valuesList.add(formatMilligrams(cholesterol.doubleValue() * quantity.doubleValue()));
        }
        if (sodium != null) {
            keysList.add("Sodium");
            valuesList.add(formatMilligrams(sodium.doubleValue() * quantity.doubleValue()));
        }
        if (carbohydrates != null) {
            keysList.add("Carbohydrates");
            valuesList.add(formatMilligrams(carbohydrates.doubleValue() * quantity.doubleValue()));
        }
        if (dietary_fiber != null) {
            keysList.add("Dietary Fiber");
            valuesList.add(formatMilligrams(dietary_fiber.doubleValue() * quantity.doubleValue()));
        }
        if (sugars != null) {
            keysList.add("Sugars");
            valuesList.add(formatMilligrams(sugars.doubleValue() * quantity.doubleValue()));
        }
        if (protein != null) {
            keysList.add("Protein");
            valuesList.add(formatMilligrams(protein.doubleValue() * quantity.doubleValue()));
        }

        new DetailsDialog(labelText, keysList.toArray(new String[0]), valuesList.toArray(new String[0]));
    }

    public void getDetails(String labelText) {
        getDetails(labelText, null);
    }

    public void getDetails() {
        getDetails(toString());
    }

    private String capitalize(String string) {
        return Stream.of(string.trim().split("\\s"))
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public String getFormattedName() {
        return capitalize(name);
    }

    public String getFormattedUnit() {
        return capitalize(unit);
    }

    public String getFormattedQuantity(Number quantity) {
        return "%s %s(s) of %s".formatted(quantity, unit.toLowerCase(), getFormattedName());
    }

    @Override
    public String toString() {
        return "%s of %s".formatted(getFormattedUnit(), getFormattedName());
    }
}
