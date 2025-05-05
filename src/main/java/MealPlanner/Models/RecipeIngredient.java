package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeIngredient extends Model {
    @Ignore public static final String TABLE = "recipe_ingredient";

    @PrimaryKey public Number recipe_id;
    @PrimaryKey public Number food_id;
    public Number quantity;
}
