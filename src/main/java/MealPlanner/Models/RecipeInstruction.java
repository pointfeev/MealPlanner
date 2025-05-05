package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.OrderBy;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeInstruction extends Model {
    @Ignore public static final String TABLE = "recipe_instruction";

    @PrimaryKey public Number recipe_id;
    @PrimaryKey @OrderBy("ASC") public Number step;
    public String instruction;
}
