package MealPlanner.Forms;

import MealPlanner.DatabaseHelper;
import MealPlanner.Forms.Components.PlaceholderTextField;
import MealPlanner.Models.FoodItem;
import MealPlanner.Models.Recipe;
import MealPlanner.Models.RecipeIngredient;
import MealPlanner.Models.RecipeInstruction;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static MealPlanner.Main.*;

public class MainFrame extends JFrame {
    public JPanel contentPane;
    public PlaceholderTextField recipeSearchField;
    public JTabbedPane tabbedPane;
    public JScrollPane recipesScrollPane;
    public JPanel recipesPanel;

    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Meal Planner");
        setMinimumSize(new Dimension(400, 400));
        setLocationByPlatform(true);

        $$$setupUI$$$();

        setupRecipeTab();
        setupMealPlansTab();
        setupFridgeTab();
        setupShoppingListTab();

        setContentPane(contentPane);
        setVisible(true);

        initialize();

        populateRecipeTab(); // initial tab
        tabbedPane.addChangeListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0:
                    populateRecipeTab();
                    break;
                case 1:
                    populateMealPlansTab();
                    break;
                case 2:
                    populateFridgeTab();
                    break;
                case 3:
                    populateShoppingListTab();
                    break;
                default:
                    tabbedPane.setSelectedIndex(0);
                    break;
            }
        });
    }

    public void initialize() {
        createOrUpdateDialog("Initializing", JOptionPane.INFORMATION_MESSAGE, new Object[]{"Cancel"}, "Initializing...");

        AtomicBoolean success = new AtomicBoolean(false);
        new Thread(() -> {
            createOrUpdateDialog(null, null, null, "Connecting to the database...");
            if (!DatabaseHelper.connect()) {
                return;
            }

            createOrUpdateDialog(null, null, null, "Setting up the database...");
            if (!DatabaseHelper.setup()) {
                return;
            }

            success.set(true);
            dialog.dispose();
        }).start();

        dialog.setVisible(true);
        if (!success.get() && dialogPane.getValue() != null) {
            System.exit(0);
        }
    }

    public void setupRecipeTab() {
        recipesPanel = new JPanel();
        recipesPanel.setLayout(new BoxLayout(recipesPanel, BoxLayout.Y_AXIS));
        recipesPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        recipesScrollPane.setViewportView(recipesPanel);

        recipeSearchField.addActionListener(event -> {
            // TODO
        });
    }

    public void populateRecipeTab() {
        recipesPanel.removeAll();

        Recipe[] recipes = new Recipe().select();
        if (recipes == null) {
            return;
        }

        for (Recipe recipe : recipes) {
            RecipePanel recipePanel = new RecipePanel();
            recipePanel.categoryLabel.setText(recipe.category);
            recipePanel.nameLabel.setText(recipe.name);
            recipePanel.editButton.addActionListener(event -> {
                // TODO
            });

            RecipeIngredient recipeIngredientCriteria = new RecipeIngredient();
            recipeIngredientCriteria.recipe_id = recipe.id;
            RecipeIngredient[] recipeIngredients = recipeIngredientCriteria.select();
            for (RecipeIngredient recipeIngredient : recipeIngredients) {
                FoodItem foodItemCriteria = new FoodItem();
                foodItemCriteria.id = recipeIngredient.food_id;
                FoodItem[] foodItems = foodItemCriteria.select();
                if (foodItems == null || foodItems.length == 0) {
                    continue;
                }
                FoodItem foodItem = foodItems[0];

                RecipeIngredientPanel recipeIngredientPanel = new RecipeIngredientPanel();
                recipeIngredientPanel.label.setText("%s %s(s) of %s".formatted(recipeIngredient.quantity, foodItem.unit, foodItem.name));

                recipeIngredientPanel.updateSize();
                recipePanel.contentPane.add(recipeIngredientPanel.contentPane);
            }

            RecipeInstruction recipeInstructionCriteria = new RecipeInstruction();
            recipeInstructionCriteria.recipe_id = recipe.id;
            RecipeInstruction[] recipeInstructions = recipeInstructionCriteria.select();
            for (RecipeInstruction recipeInstruction : recipeInstructions) {
                RecipeInstructionPanel recipeInstructionPanel = new RecipeInstructionPanel();
                recipeInstructionPanel.label.setText("%s. %s".formatted(recipeInstruction.step, recipeInstruction.instruction));

                recipeInstructionPanel.updateSize();
                recipePanel.contentPane.add(recipeInstructionPanel.contentPane);
            }

            recipePanel.updateSize();
            recipesPanel.add(recipePanel.contentPane);
        }
    }

    public void setupMealPlansTab() {
        // TODO
    }

    public void populateMealPlansTab() {
        // TODO
    }

    public void setupFridgeTab() {
        // TODO
    }

    public void populateFridgeTab() {
        // TODO
    }

    public void setupShoppingListTab() {
        // TODO
    }

    public void populateShoppingListTab() {
        // TODO
    }

    /**
     * Used by IntelliJ IDEA GUI Designer, for custom components
     */
    public void createUIComponents() {
        recipeSearchField = new PlaceholderTextField();
        recipeSearchField.setPlaceholder("Search for a recipe...");
        recipeSearchField.setPreferredSize(new Dimension(300, 30));
        recipeSearchField.setMinimumSize(new Dimension(300, 30));
        recipeSearchField.setMaximumSize(new Dimension(300, 30));
        recipeSearchField.setColumns(10);
        recipeSearchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        recipeSearchField.setOpaque(false);
        recipeSearchField.setMargin(new Insets(5, 5, 5, 5));
        recipeSearchField.setFocusable(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        tabbedPane = new JTabbedPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(tabbedPane, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Recipes", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.NORTH);
        panel2.add(recipeSearchField, BorderLayout.CENTER);
        recipesScrollPane = new JScrollPane();
        recipesScrollPane.setHorizontalScrollBarPolicy(30);
        panel1.add(recipesScrollPane, BorderLayout.CENTER);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Meal Plans", panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        tabbedPane.addTab("Fridge", panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        tabbedPane.addTab("Shopping List", panel5);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
