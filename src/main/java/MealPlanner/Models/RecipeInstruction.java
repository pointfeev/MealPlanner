package MealPlanner.Models;

import MealPlanner.Models.Annotations.*;

public class RecipeInstruction extends Model {
    @Ignore public static final String TABLE = "recipe_instruction";

    @PrimaryKey public Number id;
    @NotNull public Number recipe_id;
    @NotNull @OrderBy("ASC") @CheckNumberGreaterThan(0) public Number step;
    @NotNull public String instruction;
}
