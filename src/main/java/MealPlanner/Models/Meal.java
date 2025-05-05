package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class Meal extends Model {
    @Ignore public static final String TABLE = "MEAL";

    @PrimaryKey public int id;
    public int plan_id;
    public int day;
    public String type;
    public int recipe_id;
}
