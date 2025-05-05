package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FridgeItem extends Model {
    @Ignore public static final String TABLE = "FRIDGE_ITEM";

    @PrimaryKey public Number id;
    public Number food_id;
    public Number quantity;
}
