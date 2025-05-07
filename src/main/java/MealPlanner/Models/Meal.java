package MealPlanner.Models;

import MealPlanner.Models.Annotations.*;

public class Meal extends Model {
    @Ignore public static final String TABLE = "meal";

    @PrimaryKey public Number id;
    @NotNull public Number plan_id;
    @NotNull @OrderBy("ASC") @CheckNumber(min = 1, max = 7) public Number day;
    @NotNull @OrderBy("ASC") @CheckString({"breakfast", "lunch", "dinner"}) public String type;
    @NotNull @OrderBy("ASC") public Number recipe_id;

    @Ignore private Recipe recipe;

    public Recipe getRecipe() {
        if (recipe == null) {
            Recipe recipeCriteria = new Recipe();
            recipeCriteria.id = recipe_id;
            Recipe[] recipes = recipeCriteria.select();
            if (recipes == null || recipes.length == 0) {
                throw new RuntimeException("Meal does not have a recipe associated with it! Meal ID: %s, Recipe ID: %s".formatted(id, recipe_id));
            }
            recipe = recipes[0];
        }
        return recipe;
    }

    public void clearCache() {
        recipe = null;
    }
}
