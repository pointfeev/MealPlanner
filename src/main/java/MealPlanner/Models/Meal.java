package MealPlanner.Models;

import MealPlanner.Models.Annotations.*;

public class Meal extends Model {
    @Ignore public static final String TABLE = "meal";

    @PrimaryKey @NotNull public Number id;
    @NotNull public Number plan_id;
    @NotNull @CheckNumber(min = 1, max = 7) public Number day;
    @NotNull @CheckString({"breakfast", "lunch", "dinner"}) public String type;
    @NotNull public Number recipe_id;
}
