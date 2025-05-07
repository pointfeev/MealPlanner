package MealPlanner.Models;

import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.NotNull;
import MealPlanner.Models.Annotations.OrderBy;
import MealPlanner.Models.Annotations.PrimaryKey;

import java.sql.Date;
import java.time.LocalDate;

public class MealPlan extends Model {
    @Ignore public static final String TABLE = "meal_plan";

    @PrimaryKey public Number id;
    @NotNull public String name;
    @NotNull @OrderBy("ASC") public Date week_start;

    @Ignore private Date weekEnd;
    @Ignore private Meal[] meals;

    public Date getWeekEnd() {
        if (weekEnd == null) {
            LocalDate weekStartLocalDate = week_start.toLocalDate();
            weekEnd = Date.valueOf(weekStartLocalDate.plusDays(6));
        }
        return weekEnd;
    }

    public Meal[] getMeals() {
        if (meals == null) {
            Meal mealCriteria = new Meal();
            mealCriteria.plan_id = id;
            meals = mealCriteria.select();
            if (meals == null) {
                throw new RuntimeException("Failed to obtain meal plan associated meals! Plan ID: %s".formatted(id));
            }
        }
        return meals;
    }

    public void clearCache() {
        weekEnd = null;
        meals = null;
    }
}
