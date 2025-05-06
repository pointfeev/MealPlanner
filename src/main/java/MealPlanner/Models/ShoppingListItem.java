package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;

public class ShoppingListItem extends Model {
    @Ignore public static final String TABLE = "shopping_list_item";

    public String name;
    public String unit;
    public Number quantity;
}
