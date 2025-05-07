package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.PrimaryKey;

public class Recipe extends Model {
    @Ignore public static final String TABLE = "recipe";

    @PrimaryKey public Number id;
    @NotNull public String name;
    public String category;

    @Ignore private RecipeIngredient[] ingredients;
    @Ignore private RecipeInstruction[] instructions;

    public RecipeIngredient[] getIngredients() {
        if (ingredients == null) {
            RecipeIngredient recipeIngredientCriteria = new RecipeIngredient();
            recipeIngredientCriteria.recipe_id = id;
            ingredients = recipeIngredientCriteria.select();
        }
        return ingredients;
    }

    public RecipeInstruction[] getInstructions() {
        if (instructions == null) {
            RecipeInstruction recipeInstructionCriteria = new RecipeInstruction();
            recipeInstructionCriteria.recipe_id = id;
            instructions = recipeInstructionCriteria.select();
        }
        return instructions;
    }

    public void clearCache() {
        ingredients = null;
        instructions = null;
    }
}
