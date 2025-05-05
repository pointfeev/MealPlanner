package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

import java.sql.Date;

public class MealPlan extends Model {
    @Ignore public static final String TABLE = "meal_plan";

    @PrimaryKey public Number id;
    public String name;
    public Date week_start;
}
