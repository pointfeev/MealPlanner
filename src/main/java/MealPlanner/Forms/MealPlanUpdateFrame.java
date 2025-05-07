package MealPlanner.Forms;

import MealPlanner.Main;
import MealPlanner.Models.Meal;
import MealPlanner.Models.MealPlan;
import MealPlanner.Models.Recipe;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Locale;

public class MealPlanUpdateFrame extends JDialog {
    public MealPlan mealPlan;

    public JPanel contentPane;
    public JPanel topPane;
    public JLabel label;

    public JPanel mealsPanel;

    public ArrayList<Meal> meals;

    public MealPlanUpdateFrame(MealPlan mealPlan) {
        super(Main.mainFrame, "Meal Planner - %s Meal Plan".formatted(mealPlan == null ? "New" : "Edit"), true);
        setResizable(false);

        $$$setupUI$$$();

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(topPane);

        label.setText("%s weekly meal plan...".formatted(mealPlan == null ? "Adding new" : "Editing existing"));
        this.mealPlan = mealPlan == null ? new MealPlan() : mealPlan;

        contentPane.add(new InputPanel("Name", this.mealPlan.name == null ? "" : this.mealPlan.name, text -> this.mealPlan.name = text).contentPane);
        contentPane.add(new InputPanel("Week Start", this.mealPlan.week_start == null ? "" : this.mealPlan.week_start.toString(), text -> {
            Date parsedDate;
            try {
                parsedDate = Date.valueOf(text);
            } catch (IllegalArgumentException exception) {
                this.mealPlan.week_start = null;
                return;
            }
            this.mealPlan.week_start = parsedDate;
        }).contentPane);

        JSeparator mealsSeparator = new JSeparator();
        mealsSeparator.setMaximumSize(new Dimension(0, 5));
        contentPane.add(mealsSeparator);

        meals = new ArrayList<>();
        contentPane.add(new HeaderPanel("Meals").contentPane);

        mealsPanel = new JPanel();
        mealsPanel.setLayout(new BoxLayout(mealsPanel, BoxLayout.Y_AXIS));
        contentPane.add(mealsPanel);
        if (mealPlan != null) {
            for (Meal meal : mealPlan.getMeals()) {
                addMeal(meal);
            }
        }
        contentPane.add(new ButtonPanel("New Meal", event -> addMeal(null)).contentPane);

        JPanel actionPanel = new JPanel();
        actionPanel.setAlignmentX(0.0f);
        FlowLayout actionPanelLayout = new FlowLayout(FlowLayout.CENTER, 0, 5);
        actionPanel.setLayout(actionPanelLayout);
        contentPane.add(actionPanel);

        ButtonPanel saveButtonPanel = new ButtonPanel("Save", event -> {
            if (!this.mealPlan.validate()) {
                return;
            }

            boolean success;
            if (this.mealPlan.id == null) {
                success = this.mealPlan.insert();
            } else {
                for (Meal meal : this.mealPlan.getMeals()) {
                    if (!meals.contains(meal)) {
                        meal.delete();
                    }
                }

                success = this.mealPlan.update();
            }
            if (!success) {
                return;
            }

            for (Meal meal : meals) {
                meal.plan_id = this.mealPlan.id;
                if (!meal.validate()) {
                    return;
                }

                if (meal.id == null) {
                    success = meal.insert();
                } else {
                    success = meal.update();
                }
                if (!success) {
                    return;
                }
            }

            dispose();
        });
        actionPanel.add(saveButtonPanel.contentPane);

        if (mealPlan != null) {
            ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
                mealPlan.delete();

                dispose();
            });
            actionPanel.add(deleteButtonPanel.contentPane);
        }

        ButtonPanel cancelButtonPanel = new ButtonPanel("Cancel", event -> dispose());
        actionPanel.add(cancelButtonPanel.contentPane);

        setContentPane(contentPane);

        pack();
        setLocationRelativeTo(Main.mainFrame);

        setVisible(true);
    }

    private void addMeal(Meal meal) {
        Recipe recipe;
        if (meal == null) {
            RecipeSelectFrame recipeSelectFrame = new RecipeSelectFrame();
            recipe = recipeSelectFrame.selectedRecipe;
            if (recipe == null) {
                return;
            }

            meal = new Meal();
            meal.plan_id = mealPlan.id;
            meal.recipe_id = recipe.id;
        } else {
            recipe = meal.getRecipe();
        }
        Meal mealFinal = meal; // for lambdas
        meals.add(meal);

        JPanel recipePanel = new JPanel();
        recipePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        recipePanel.setAlignmentX(0.0f);
        mealsPanel.add(recipePanel);

        InputPanel foodNameInputPanel = new InputPanel("Recipe", recipe.name, null, 20, false);
        recipePanel.add(foodNameInputPanel.contentPane);

        ButtonPanel editButtonPanel = new ButtonPanel("Edit", event -> {
            RecipeSelectFrame recipeSelectFrame = new RecipeSelectFrame();
            Recipe selectedRecipe = recipeSelectFrame.selectedRecipe;
            if (selectedRecipe == null) {
                return;
            }

            mealFinal.recipe_id = selectedRecipe.id;
            foodNameInputPanel.inputField.setText(selectedRecipe.name);
        });
        editButtonPanel.contentPane.remove(editButtonPanel.leftSeparator);
        recipePanel.add(editButtonPanel.contentPane);

        InputPanel dayInputPanel = new InputPanel("Day", meal.day == null ? "" : meal.day.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Integer.parseInt(text);
            } catch (NumberFormatException exception) {
                mealFinal.day = null;
                return;
            }
            mealFinal.day = parsedNumber;
        }, 5);
        InputPanel typeInputPanel = new InputPanel("Type", meal.type == null ? "" : meal.type, text -> mealFinal.type = text, 5);

        ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
            meals.remove(mealFinal);

            mealsPanel.remove(recipePanel);
            mealsPanel.remove(dayInputPanel.contentPane);
            mealsPanel.remove(typeInputPanel.contentPane);

            pack();
        });
        deleteButtonPanel.contentPane.remove(deleteButtonPanel.leftSeparator);
        recipePanel.add(deleteButtonPanel.contentPane);

        mealsPanel.add(dayInputPanel.contentPane);
        mealsPanel.add(typeInputPanel.contentPane);

        pack();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        topPane = new JPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        topPane.setAlignmentX(0.0f);
        final JSeparator separator1 = new JSeparator();
        separator1.setPreferredSize(new Dimension(10, 0));
        topPane.add(separator1);
        label = new JLabel();
        Font labelFont = this.$$$getFont$$$(null, Font.BOLD, -1, label.getFont());
        if (labelFont != null) label.setFont(labelFont);
        label.setText("Label");
        topPane.add(label);
        final JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(10, 0));
        topPane.add(separator2);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return topPane;
    }

}
