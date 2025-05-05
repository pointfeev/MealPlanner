package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FridgeItem extends Model {
    @Ignore public static final String TABLE = "FRIDGE_ITEM";

    @PrimaryKey public int id;
    public int food_id;
    public int quantity;
}
