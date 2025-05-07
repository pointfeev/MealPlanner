package MealPlanner.GUI.FoodItems;

import MealPlanner.GUI.Components.ButtonPanel;
import MealPlanner.GUI.Components.InputPanel;
import MealPlanner.Main;
import MealPlanner.Models.FoodItem;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

public class FoodItemUpdateDialog extends JDialog {
    public FoodItem foodItem;

    public JPanel contentPane;
    public JPanel topPane;
    public JLabel label;

    public FoodItemUpdateDialog(FoodItem foodItem) {
        super(Main.mainFrame, "Meal Planner - %s Item".formatted(foodItem == null ? "New" : "Edit"), true);
        setResizable(false);

        $$$setupUI$$$();

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(topPane);

        label.setText("%s item...".formatted(foodItem == null ? "Adding new" : "Editing existing"));
        this.foodItem = foodItem == null ? new FoodItem() : foodItem;

        contentPane.add(new InputPanel("Name", this.foodItem.name == null ? "" : this.foodItem.name, text -> this.foodItem.name = text, 20).contentPane);
        contentPane.add(new InputPanel("Food Group", this.foodItem.food_group == null ? "" : this.foodItem.food_group, text -> this.foodItem.food_group = text.toLowerCase(), 10).contentPane);
        contentPane.add(new InputPanel("Unit", this.foodItem.unit == null ? "" : this.foodItem.unit, text -> this.foodItem.unit = text.toLowerCase(), 10).contentPane);
        contentPane.add(new InputPanel("Calories", this.foodItem.calories == null ? "0" : this.foodItem.calories.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.calories = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.calories = null;
                return;
            }
            this.foodItem.calories = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Fat", "milligrams", this.foodItem.fat == null ? "0" : this.foodItem.fat.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.fat = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.fat = null;
                return;
            }
            this.foodItem.fat = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Cholesterol", "milligrams", this.foodItem.cholesterol == null ? "0" : this.foodItem.cholesterol.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.cholesterol = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.cholesterol = null;
                return;
            }
            this.foodItem.cholesterol = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Sodium", "milligrams", this.foodItem.sodium == null ? "0" : this.foodItem.sodium.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.sodium = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.sodium = null;
                return;
            }
            this.foodItem.sodium = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Carbohydrates", "milligrams", this.foodItem.carbohydrates == null ? "0" : this.foodItem.carbohydrates.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.carbohydrates = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.carbohydrates = null;
                return;
            }
            this.foodItem.carbohydrates = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Dietary Fiber", "milligrams", this.foodItem.dietary_fiber == null ? "0" : this.foodItem.dietary_fiber.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.dietary_fiber = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.dietary_fiber = null;
                return;
            }
            this.foodItem.dietary_fiber = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Sugars", "milligrams", this.foodItem.sugars == null ? "0" : this.foodItem.sugars.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.sugars = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.sugars = null;
                return;
            }
            this.foodItem.sugars = parsedNumber;
        }, 5).contentPane);
        contentPane.add(new InputPanel("Protein", "milligrams", this.foodItem.protein == null ? "0" : this.foodItem.protein.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.foodItem.protein = null;
                return;
            }
            if (parsedNumber.doubleValue() == 0) {
                this.foodItem.protein = null;
                return;
            }
            this.foodItem.protein = parsedNumber;
        }, 5).contentPane);

        JPanel actionPanel = new JPanel();
        actionPanel.setAlignmentX(0.0f);
        FlowLayout actionPanelLayout = new FlowLayout(FlowLayout.CENTER, 0, 5);
        actionPanel.setLayout(actionPanelLayout);
        contentPane.add(actionPanel);

        ButtonPanel saveButtonPanel = new ButtonPanel("Save", event -> {
            if (!this.foodItem.validate()) {
                return;
            }

            boolean success;
            if (this.foodItem.id == null) {
                success = this.foodItem.insert();
            } else {
                success = this.foodItem.update();
            }
            if (!success) {
                return;
            }

            dispose();
        });
        actionPanel.add(saveButtonPanel.contentPane);

        if (foodItem != null) {
            ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
                int option = JOptionPane.showOptionDialog(this, "Are you sure you want to delete this item?" +
                                "\n\nRecipe ingredients and fridge/pantry items associated with this item will also be deleted!",
                        "Meal Planner - Delete Item", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
                foodItem.delete();

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
