package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FoodItem extends Model {
    @Ignore public static final String TABLE = "FOOD_ITEM";

    @PrimaryKey public Number id;
    public String name;
    public String food_group;
    public String unit;
    public int calories;
    public int fat;
    public int cholesterol;
    public int sodium;
    public int carbohydrates;
    public int dietary_fiber;
    public int sugars;
    public int protein;
}
