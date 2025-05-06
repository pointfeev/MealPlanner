package MealPlanner.Forms;

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

public class RecipeUpdateFrame extends JDialog {
    public Recipe recipe;
    public Recipe recipeToSave;

    public JPanel contentPane;
    public JPanel topPane;
    public JLabel label;

    public JPanel ingredientsPanel;
    public JPanel instructionsPanel;

    public ArrayList<RecipeIngredient> ingredients;
    public ArrayList<RecipeIngredient> instructions;

    public RecipeUpdateFrame(Recipe recipe) {
        super(Main.mainFrame, "Meal Planner - %s Recipe".formatted(recipe == null ? "New" : "Edit"), true);
        setResizable(false);

        $$$setupUI$$$();

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(topPane);

        this.recipe = recipe;
        recipeToSave = recipe == null ? new Recipe() : recipe;
        label.setText("%s recipe...".formatted(recipe == null ? "Adding new" : "Editing existing"));

        contentPane.add(new InputPanel("Name", recipe == null ? "" : recipe.name, 30).contentPane);
        contentPane.add(new InputPanel("Category", recipe == null ? "" : recipe.category, 30).contentPane);

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
            // TODO: set values on recipeToSave from input boxes
            //       may want to add listeners to input boxes instead for this
            //       same for ingredients and instructions

            // ArrayList<RecipeIngredient> ingredientsToRemove = new ArrayList<>(Arrays.asList(recipeToSave.getIngredients()));
            // ingredientsToRemove.removeAll(ingredients);
            // System.out.println(ingredientsToRemove);
            // for (RecipeInstruction instruction : recipe.getInstructions()) {
            //     addInstruction(instruction);
            // }

            dispose();
        });
        actionPanel.add(saveButtonPanel.contentPane);

        ButtonPanel cancelButtonPanel = new ButtonPanel("Cancel", event -> dispose());
        actionPanel.add(cancelButtonPanel.contentPane);

        setContentPane(contentPane);

        pack();
        setLocationRelativeTo(Main.mainFrame);

        setVisible(true);
    }

    private void addIngredient(RecipeIngredient ingredient) {
        // TODO: if ingredient null

        FoodItem foodItem = ingredient.getFoodItem();

        InputPanel foodIdInputPanel = new InputPanel("ID", ingredient.food_id.toString(), 30);
        foodIdInputPanel.contentPane.setVisible(false);
        ingredientsPanel.add(foodIdInputPanel.contentPane);

        JPanel foodPanel = new JPanel();
        foodPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        foodPanel.setAlignmentX(0.0f);
        ingredientsPanel.add(foodPanel);

        InputPanel foodNameInputPanel = new InputPanel("Item", "%s of %s".formatted(
                foodItem.unit.substring(0, 1).toUpperCase() + foodItem.unit.substring(1).toLowerCase(),
                foodItem.name), 20, false);
        foodPanel.add(foodNameInputPanel.contentPane);

        ButtonPanel editButtonPanel = new ButtonPanel("Edit", event -> {
            // TODO
        });
        editButtonPanel.contentPane.remove(editButtonPanel.leftSeparator);
        foodPanel.add(editButtonPanel.contentPane);

        InputPanel stepInputPanel = new InputPanel("Quantity", ingredient.quantity.toString(), 5);

        ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
            ingredientsPanel.remove(foodIdInputPanel.contentPane);
            ingredientsPanel.remove(foodPanel);
            ingredientsPanel.remove(stepInputPanel.contentPane);

            pack();
        });
        deleteButtonPanel.contentPane.remove(deleteButtonPanel.leftSeparator);
        foodPanel.add(deleteButtonPanel.contentPane);

        ingredientsPanel.add(stepInputPanel.contentPane);
    }

    private void addInstruction(RecipeInstruction instruction) {
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        instructionPanel.setAlignmentX(0.0f);
        instructionsPanel.add(instructionPanel);

        instructionPanel.add(new InputPanel("Step #", instruction == null ? "" : instruction.step.toString(), 5).contentPane);

        InputPanel instructionInputPanel = new InputPanel("Instruction", instruction == null ? "" : instruction.instruction, 30);

        ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
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
