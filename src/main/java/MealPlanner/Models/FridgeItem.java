package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.PrimaryKey;

public class FridgeItem extends Model {
    @Ignore public static final String TABLE = "fridge_item";

    @PrimaryKey @NotNull public Number id;
    @NotNull public Number food_id;
    @NotNull public Number quantity;
}
