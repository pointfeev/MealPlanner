package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.OrderBy;
import MealPlanner.Models.Annotations.PrimaryKey;

public class RecipeInstruction extends Model {
    @Ignore public static final String TABLE = "recipe_instruction";

    @PrimaryKey @NotNull public Number recipe_id;
    @PrimaryKey @NotNull @OrderBy("ASC") public Number step;
    @NotNull public String instruction;
}
