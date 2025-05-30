package MealPlanner.GUI.Recipes;

import MealPlanner.GUI.Components.Panel;
import MealPlanner.Models.RecipeInstruction;

import javax.swing.*;
import java.awt.*;

public class RecipeInstructionPanel extends Panel {
    public RecipeInstruction recipeInstruction;

    public JPanel contentPane;
    public JLabel label;

    public RecipeInstructionPanel(RecipeInstruction recipeInstruction) {
        $$$setupUI$$$();

        this.recipeInstruction = recipeInstruction;
        label.setText("%s. %s".formatted(recipeInstruction.step, recipeInstruction.instruction));

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
        contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
        contentPane.setAlignmentX(0.0f);
        final JSeparator separator1 = new JSeparator();
        separator1.setPreferredSize(new Dimension(20, 0));
        contentPane.add(separator1);
        label = new JLabel();
        label.setHorizontalAlignment(0);
        label.setHorizontalTextPosition(0);
        label.setText("Label");
        contentPane.add(label);
        final JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(10, 0));
        contentPane.add(separator2);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
