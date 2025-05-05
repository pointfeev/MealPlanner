package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeIngredient extends Model {
    @Ignore public static final String TABLE = "RECIPE_INGREDIENT";

    @PrimaryKey public int recipe_id;
    @PrimaryKey public int food_id;
    public int quantity;
}
