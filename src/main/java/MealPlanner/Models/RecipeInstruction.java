package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeInstruction extends Model {
    @Ignore public static final String TABLE = "RECIPE_INSTRUCTION";

    @PrimaryKey public Number recipe_id;
    @PrimaryKey public Number step;
    public String instruction;
}
