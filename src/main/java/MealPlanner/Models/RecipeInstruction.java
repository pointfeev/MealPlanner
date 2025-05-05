package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeInstruction extends Model {
    @Ignore public static final String TABLE = "RECIPE_INSTRUCTION";

    @PrimaryKey public int recipe_id;
    @PrimaryKey public int step;
    public String instruction;
}
