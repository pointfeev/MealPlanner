package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class Recipe extends Model {
    @Ignore public static final String TABLE = "RECIPE";

    @PrimaryKey public int id;
    public String name;
    public String category;
}
