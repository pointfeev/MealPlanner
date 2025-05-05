package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class Meal extends Model {
    @Ignore public static final String TABLE = "MEAL";

    @PrimaryKey public Number id;
    public Number plan_id;
    public Number day;
    public String type;
    public Number recipe_id;
}
