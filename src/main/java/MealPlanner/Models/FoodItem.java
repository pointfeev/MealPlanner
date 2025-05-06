package MealPlanner.Models;

import MealPlanner.Models.Annotations.CheckString;
import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FoodItem extends Model {
    @Ignore public static final String TABLE = "food_item";

    @PrimaryKey @NotNull public Number id;
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
}
