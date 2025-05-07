package MealPlanner.GUI.Recipes;

import MealPlanner.GUI.Components.ButtonPanel;
import MealPlanner.GUI.Components.HeaderPanel;
import MealPlanner.GUI.Components.InputPanel;
import MealPlanner.GUI.FoodItems.FoodItemSelectDialog;
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

public class RecipeUpdateDialog extends JDialog {
    public Recipe recipe;

    public JPanel contentPane;
    public JPanel topPane;
    public JLabel label;

    public JPanel ingredientsPanel;
    public JPanel instructionsPanel;

    public ArrayList<RecipeIngredient> ingredients;
    public ArrayList<RecipeInstruction> instructions;

    public RecipeUpdateDialog(Recipe recipe) {
        super(Main.mainFrame, "Meal Planner - %s Recipe".formatted(recipe == null ? "New" : "Edit"), true);
        setResizable(false);

        $$$setupUI$$$();

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(topPane);

        label.setText("%s recipe...".formatted(recipe == null ? "Adding new" : "Editing existing"));
        this.recipe = recipe == null ? new Recipe() : recipe;

        contentPane.add(new InputPanel("Name", this.recipe.name == null ? "" : this.recipe.name, text -> this.recipe.name = text).contentPane);
        contentPane.add(new InputPanel("Category", this.recipe.name == null ? "" : this.recipe.category, text -> this.recipe.category = text, 20).contentPane);

        JSeparator ingredientSeparator = new JSeparator();
        ingredientSeparator.setMaximumSize(new Dimension(0, 5));
        contentPane.add(ingredientSeparator);

        ingredients = new ArrayList<>();
        contentPane.add(new HeaderPanel("Ingredients").contentPane);

        ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        contentPane.add(ingredientsPanel);
        if (recipe != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                addIngredient(ingredient);
            }
        }
        contentPane.add(new ButtonPanel("New Ingredient", event -> addIngredient(null)).contentPane);

        JSeparator instructionSeparator = new JSeparator();
        instructionSeparator.setMaximumSize(new Dimension(0, 5));
        contentPane.add(instructionSeparator);

        instructions = new ArrayList<>();
        contentPane.add(new HeaderPanel("Instructions").contentPane);

        instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        contentPane.add(instructionsPanel);
        if (recipe != null) {
            for (RecipeInstruction instruction : recipe.getInstructions()) {
                addInstruction(instruction);
            }
        }
        contentPane.add(new ButtonPanel("New Instruction", event -> addInstruction(null)).contentPane);

        JPanel actionPanel = new JPanel();
        actionPanel.setAlignmentX(0.0f);
        FlowLayout actionPanelLayout = new FlowLayout(FlowLayout.CENTER, 0, 5);
        actionPanel.setLayout(actionPanelLayout);
        contentPane.add(actionPanel);

        ButtonPanel saveButtonPanel = new ButtonPanel("Save", event -> {
            if (!this.recipe.validate()) {
                return;
            }
            for (RecipeIngredient ingredient : ingredients) {
                Number oldId = ingredient.recipe_id;
                ingredient.recipe_id = 1; // so validation skips recipe_id
                try {
                    if (!ingredient.validate()) {
                        return;
                    }
                } finally {
                    ingredient.recipe_id = oldId;
                }
            }
            for (RecipeInstruction instruction : instructions) {
                Number oldId = instruction.recipe_id;
                instruction.recipe_id = 1; // so validation skips recipe_id
                try {
                    if (!instruction.validate()) {
                        return;
                    }
                } finally {
                    instruction.recipe_id = oldId;
                }
            }

            boolean success;
            if (this.recipe.id == null) {
                success = this.recipe.insert();
            } else {
                for (RecipeIngredient ingredient : this.recipe.getIngredients()) {
                    boolean shouldDelete = true;
                    for (RecipeIngredient ingredientUpdated : ingredients) {
                        if (ingredientUpdated.id != null && ingredientUpdated.id.intValue() == ingredient.id.intValue()) {
                            shouldDelete = false;
                            break;
                        }
                    }
                    if (shouldDelete) {
                        ingredient.delete();
                    }
                }

                for (RecipeInstruction instruction : this.recipe.getInstructions()) {
                    boolean shouldDelete = true;
                    for (RecipeInstruction instructionUpdated : instructions) {
                        if (instructionUpdated.id != null && instructionUpdated.id.intValue() == instruction.id.intValue()) {
                            shouldDelete = false;
                            break;
                        }
                    }
                    if (shouldDelete) {
                        instruction.delete();
                    }
                }

                success = this.recipe.update();
            }
            if (!success) {
                return;
            }

            for (RecipeIngredient ingredient : ingredients) {
                ingredient.recipe_id = this.recipe.id;
                if (ingredient.id == null) {
                    success = success && ingredient.insert();
                } else {
                    success = success && ingredient.update();
                }
            }

            for (RecipeInstruction instruction : instructions) {
                instruction.recipe_id = this.recipe.id;
                if (instruction.id == null) {
                    success = success && instruction.insert();
                } else {
                    success = success && instruction.update();
                }
            }

            if (!success) {
                return;
            }
            dispose();
        });
        actionPanel.add(saveButtonPanel.contentPane);

        if (recipe != null) {
            ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
                int option = JOptionPane.showOptionDialog(this, "Are you sure you want to delete this recipe?" +
                                "\n\nMeals associated with this recipe will also be deleted!", "Meal Planner - Delete Recipe",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
                recipe.delete();

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

    private void addIngredient(RecipeIngredient ingredient) {
        FoodItem foodItem;
        if (ingredient == null) {
            FoodItemSelectDialog foodItemSelectDialog = new FoodItemSelectDialog();
            // TODO: update/refresh the whole ingredients panel here to resolve issues with changed food items
            foodItem = foodItemSelectDialog.selectedFoodItem;
            if (foodItem == null) {
                return;
            }

            ingredient = new RecipeIngredient();
            ingredient.food_id = foodItem.id;
            ingredient.recipe_id = recipe.id;
        } else {
            foodItem = ingredient.getFoodItem();
        }
        RecipeIngredient ingredientFinal = ingredient; // for lambdas
        ingredients.add(ingredient);

        JPanel foodPanel = new JPanel();
        foodPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        foodPanel.setAlignmentX(0.0f);
        ingredientsPanel.add(foodPanel);

        InputPanel foodNameInputPanel = new InputPanel("Item", foodItem.toString(), 20, false);
        foodPanel.add(foodNameInputPanel.contentPane);

        ButtonPanel editButtonPanel = new ButtonPanel("Edit", event -> {
            FoodItemSelectDialog foodItemSelectDialog = new FoodItemSelectDialog();
            // TODO: update/refresh the whole ingredients panel here to resolve issues with changed food items
            FoodItem selectedFoodItem = foodItemSelectDialog.selectedFoodItem;
            if (selectedFoodItem == null) {
                return;
            }

            ingredientFinal.food_id = selectedFoodItem.id;
            foodNameInputPanel.inputField.setText(selectedFoodItem.toString());
        });
        editButtonPanel.contentPane.remove(editButtonPanel.leftSeparator);
        foodPanel.add(editButtonPanel.contentPane);

        InputPanel quantityInputPanel = new InputPanel("Quantity", ingredient.quantity == null ? "" : ingredient.quantity.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                ingredientFinal.quantity = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                ingredientFinal.quantity = null;
                return;
            }
            ingredientFinal.quantity = parsedNumber;
        }, 5);

        ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
            ingredients.remove(ingredientFinal);

            ingredientsPanel.remove(foodPanel);
            ingredientsPanel.remove(quantityInputPanel.contentPane);

            pack();
        });
        deleteButtonPanel.contentPane.remove(deleteButtonPanel.leftSeparator);
        foodPanel.add(deleteButtonPanel.contentPane);

        ingredientsPanel.add(quantityInputPanel.contentPane);

        pack();
    }

    private void addInstruction(RecipeInstruction instruction) {
        if (instruction == null) {
            instruction = new RecipeInstruction();
            instruction.recipe_id = recipe.id;
        }
        RecipeInstruction instructionFinal = instruction; // for lambdas
        instructions.add(instruction);

        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        instructionPanel.setAlignmentX(0.0f);
        instructionsPanel.add(instructionPanel);

        instructionPanel.add(new InputPanel("Step #", instruction.step == null ? "" : instruction.step.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Integer.parseInt(text);
            } catch (NumberFormatException exception) {
                instructionFinal.step = null;
                return;
            }
            instructionFinal.step = parsedNumber;
        }, 5).contentPane);

        InputPanel instructionInputPanel = new InputPanel("Instruction", instruction.instruction == null ? "" : instruction.instruction, text -> instructionFinal.instruction = text);

        ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
            instructions.remove(instructionFinal);

            instructionsPanel.remove(instructionPanel);
            instructionsPanel.remove(instructionInputPanel.contentPane);

            pack();
        });
        deleteButtonPanel.contentPane.remove(deleteButtonPanel.leftSeparator);
        instructionPanel.add(deleteButtonPanel.contentPane);

        instructionsPanel.add(instructionInputPanel.contentPane);

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
