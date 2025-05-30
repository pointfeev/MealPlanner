package MealPlanner.GUI.FridgeItems;

import MealPlanner.GUI.Components.ButtonPanel;
import MealPlanner.GUI.Components.InputPanel;
import MealPlanner.GUI.FoodItems.FoodItemSelectDialog;
import MealPlanner.Main;
import MealPlanner.Models.FoodItem;
import MealPlanner.Models.FridgeItem;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Locale;

public class FridgeItemUpdateDialog extends JDialog {
    public FridgeItem fridgeItem;

    public JPanel contentPane;
    public JPanel topPane;
    public JLabel label;

    public FridgeItemUpdateDialog(FridgeItem fridgeItem) {
        super(Main.mainFrame, "Meal Planner - %s Fridge/Pantry Item".formatted(fridgeItem == null ? "New" : "Edit"), true);
        setResizable(false);

        $$$setupUI$$$();

        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(topPane);

        label.setText("%s fridge/pantry item...".formatted(fridgeItem == null ? "Adding new" : "Editing existing"));
        this.fridgeItem = fridgeItem == null ? new FridgeItem() : fridgeItem;


        JPanel foodPanel = new JPanel();
        foodPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        foodPanel.setAlignmentX(0.0f);
        contentPane.add(foodPanel);

        FoodItem foodItem = fridgeItem != null ? this.fridgeItem.getFoodItem() : null;
        InputPanel foodNameInputPanel = new InputPanel("Item", foodItem == null ? "" : foodItem.toString(), 20, false);
        foodPanel.add(foodNameInputPanel.contentPane);

        ButtonPanel editButtonPanel = new ButtonPanel("Edit", event -> {
            FoodItemSelectDialog foodItemSelectDialog = new FoodItemSelectDialog(deletedFoodItem -> {
                if (this.fridgeItem.food_id == null) {
                    return;
                }

                if (this.fridgeItem.food_id.intValue() == deletedFoodItem.id.intValue()) {
                    this.fridgeItem.id = null;
                    this.fridgeItem.food_id = null;
                    foodNameInputPanel.inputField.setText("");
                }
            });

            FoodItem selectedFoodItem = foodItemSelectDialog.selectedFoodItem;
            if (selectedFoodItem != null) {
                this.fridgeItem.food_id = selectedFoodItem.id;
            }

            if (this.fridgeItem.food_id == null) {
                return;
            }

            this.fridgeItem.clearCache();
            FoodItem updatedFoodItem = this.fridgeItem.getFoodItem();
            foodNameInputPanel.inputField.setText(updatedFoodItem == null ? "" : updatedFoodItem.toString());
        });
        editButtonPanel.contentPane.remove(editButtonPanel.leftSeparator);
        foodPanel.add(editButtonPanel.contentPane);

        contentPane.add(new InputPanel("Quantity", this.fridgeItem.quantity == null ? "" : this.fridgeItem.quantity.toString(), text -> {
            Number parsedNumber;
            try {
                parsedNumber = Double.parseDouble(text);
            } catch (NumberFormatException exception) {
                this.fridgeItem.quantity = null;
                return;
            }
            this.fridgeItem.quantity = parsedNumber;
        }, 5).contentPane);

        JPanel actionPanel = new JPanel();
        actionPanel.setAlignmentX(0.0f);
        FlowLayout actionPanelLayout = new FlowLayout(FlowLayout.CENTER, 0, 5);
        actionPanel.setLayout(actionPanelLayout);
        contentPane.add(actionPanel);

        ButtonPanel saveButtonPanel = new ButtonPanel("Save", event -> {
            if (!this.fridgeItem.validate()) {
                return;
            }

            boolean success;
            if (this.fridgeItem.id == null) {
                success = this.fridgeItem.insert();
            } else {
                success = this.fridgeItem.update();
            }
            if (!success) {
                return;
            }

            dispose();
        });
        actionPanel.add(saveButtonPanel.contentPane);

        if (fridgeItem != null) {
            ButtonPanel deleteButtonPanel = new ButtonPanel("Delete", event -> {
                int option = JOptionPane.showOptionDialog(this, "Are you sure you want to delete this fridge/pantry item?",
                        "Meal Planner - Delete Fridge/Pantry Item", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
                fridgeItem.delete();

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
