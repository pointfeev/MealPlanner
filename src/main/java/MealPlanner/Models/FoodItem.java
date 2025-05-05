package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FoodItem extends Model {
    @Ignore public static final String TABLE = "food_item";

    @PrimaryKey public Number id;
    public String name;
    public String food_group;
    public String unit;
    public Number calories;
    public Number fat;
    public Number cholesterol;
    public Number sodium;
    public Number carbohydrates;
    public Number dietary_fiber;
    public Number sugars;
    public Number protein;
}
