package MealPlanner.GUI.Recipes;

import MealPlanner.GUI.Components.Panel;
import MealPlanner.GUI.Details.DetailsDialog;
import MealPlanner.Main;
import MealPlanner.Models.FoodItem;
import MealPlanner.Models.Recipe;
import MealPlanner.Models.RecipeIngredient;
import MealPlanner.Models.RecipeInstruction;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

import static MealPlanner.Models.FoodItem.formatMilligrams;

public class RecipePanel extends Panel {
    public Recipe recipe;

    public JPanel contentPane;
    public JPanel topPane;
    public JLabel categoryLabel;
    public JLabel nameLabel;
    public JButton editButton;
    public JButton detailsButton;

    public RecipePanel(Recipe recipe) {
        $$$setupUI$$$();

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(topPane);

        this.recipe = recipe;
        categoryLabel.setText(recipe.category);
        nameLabel.setText(recipe.name);
        detailsButton.addActionListener(event -> {
            StringBuilder foodGroupsBuilder = new StringBuilder();
            double calories = 0;
            double fat = 0;
            double cholesterol = 0;
            double sodium = 0;
            double carbohydrates = 0;
            double dietary_fiber = 0;
            double sugars = 0;
            double protein = 0;

            for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
                FoodItem foodItem = recipeIngredient.getFoodItem();

                if (foodItem.food_group != null) {
                    if (!foodGroupsBuilder.isEmpty()) {
                        foodGroupsBuilder.append(", ");
                    }
                    foodGroupsBuilder.append(foodItem.food_group);
                }
                if (foodItem.calories != null) {
                    calories += foodItem.calories.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.fat != null) {
                    fat += foodItem.fat.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.cholesterol != null) {
                    cholesterol += foodItem.cholesterol.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.sodium != null) {
                    sodium += foodItem.sodium.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.carbohydrates != null) {
                    carbohydrates += foodItem.carbohydrates.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.dietary_fiber != null) {
                    dietary_fiber += foodItem.dietary_fiber.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.sugars != null) {
                    sugars += foodItem.sugars.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
                if (foodItem.protein != null) {
                    protein += foodItem.protein.doubleValue() * recipeIngredient.quantity.doubleValue();
                }
            }

            ArrayList<String> keysList = new ArrayList<>();
            ArrayList<String> valuesList = new ArrayList<>();

            if (!foodGroupsBuilder.isEmpty()) {
                keysList.add("Food Groups");
                valuesList.add(foodGroupsBuilder.toString());
            }
            if (calories != 0) {
                keysList.add("Calories");
                valuesList.add(String.valueOf(calories));
            }
            if (fat != 0) {
                keysList.add("Fat");
                valuesList.add(formatMilligrams(fat));
            }
            if (cholesterol != 0) {
                keysList.add("Cholesterol");
                valuesList.add(formatMilligrams(cholesterol));
            }
            if (sodium != 0) {
                keysList.add("Sodium");
                valuesList.add(formatMilligrams(sodium));
            }
            if (carbohydrates != 0) {
                keysList.add("Carbohydrates");
                valuesList.add(formatMilligrams(carbohydrates));
            }
            if (dietary_fiber != 0) {
                keysList.add("Dietary Fiber");
                valuesList.add(formatMilligrams(dietary_fiber));
            }
            if (sugars != 0) {
                keysList.add("Sugars");
                valuesList.add(formatMilligrams(sugars));
            }
            if (protein != 0) {
                keysList.add("Protein");
                valuesList.add(formatMilligrams(protein));
            }

            new DetailsDialog(recipe.name, keysList.toArray(new String[0]), valuesList.toArray(new String[0]));
        });
        editButton.addActionListener(event -> {
            new RecipeUpdateDialog(recipe);
            Main.mainFrame.refresh();
        });

        for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
            contentPane.add(new RecipeIngredientPanel(recipeIngredient).contentPane);
        }
        for (RecipeInstruction recipeInstruction : recipe.getInstructions()) {
            contentPane.add(new RecipeInstructionPanel(recipeInstruction).contentPane);
        }

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        contentPane.add(separator);

        updateSize(topPane);
        updateSize(contentPane);
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
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
        topPane.setAlignmentX(0.0f);
        final JSeparator separator1 = new JSeparator();
        separator1.setPreferredSize(new Dimension(10, 0));
        topPane.add(separator1);
        categoryLabel = new JLabel();
        Font categoryLabelFont = this.$$$getFont$$$(null, Font.ITALIC, -1, categoryLabel.getFont());
        if (categoryLabelFont != null) categoryLabel.setFont(categoryLabelFont);
        categoryLabel.setText("Label");
        topPane.add(categoryLabel);
        final JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(20, 0));
        topPane.add(separator2);
        nameLabel = new JLabel();
        Font nameLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, nameLabel.getFont());
        if (nameLabelFont != null) nameLabel.setFont(nameLabelFont);
        nameLabel.setHorizontalAlignment(0);
        nameLabel.setHorizontalTextPosition(0);
        nameLabel.setText("Label");
        topPane.add(nameLabel);
        final JSeparator separator3 = new JSeparator();
        separator3.setPreferredSize(new Dimension(20, 0));
        topPane.add(separator3);
        detailsButton = new JButton();
        detailsButton.setText("Details");
        topPane.add(detailsButton);
        final JSeparator separator4 = new JSeparator();
        separator4.setPreferredSize(new Dimension(10, 0));
        topPane.add(separator4);
        editButton = new JButton();
        editButton.setText("Edit");
        topPane.add(editButton);
        final JSeparator separator5 = new JSeparator();
        separator5.setPreferredSize(new Dimension(10, 0));
        topPane.add(separator5);
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
