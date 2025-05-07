package MealPlanner.Forms;

import MealPlanner.Forms.Components.PlaceholderTextField;
import MealPlanner.Models.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class MainFrame extends JFrame {
    public JPanel contentPane;
    public JTabbedPane tabbedPane;

    public PlaceholderTextField recipeSearchField;
    public JButton recipeNewButton;
    public JScrollPane recipesScrollPane;
    public JPanel recipesPanel;
    public JScrollPane fridgeScrollPane;
    public PlaceholderTextField fridgeSearchField;
    public JButton fridgeNewButton;
    public PlaceholderTextField shoppingSearchField;
    public JScrollPane shoppingScrollPane;
    public PlaceholderTextField mealPlanSearchField;
    public JButton mealPlanNewButton;
    public JScrollPane mealPlansScrollPane;
    public JCheckBox mealPlanShowPastCheckBox;
    public JPanel fridgeItemsPanel;
    public JPanel shoppingItemsPanel;
    public JPanel mealPlansPanel;
    private RecipePanel[] recipePanels;
    private FridgeItemPanel[] fridgeItemPanels;
    private ShoppingItemPanel[] shoppingItemPanels;
    private MealPlanPanel[] mealPlanPanels;

    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Meal Planner");
        setMinimumSize(new Dimension(400, 300));
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        $$$setupUI$$$();

        tabbedPane.setSelectedIndex(-1);

        setupRecipeTab();
        setupMealPlansTab();
        setupFridgeTab();
        setupShoppingListTab();

        setContentPane(contentPane);
        setVisible(true);

        tabbedPane.addChangeListener(event -> {
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
            }
        });
        tabbedPane.setSelectedIndex(0);
    }

    public void refresh() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        tabbedPane.setSelectedIndex(-1);
        tabbedPane.setSelectedIndex(selectedIndex);
    }

    public void setupRecipeTab() {
        recipesPanel = new JPanel();
        recipesPanel.setLayout(new BoxLayout(recipesPanel, BoxLayout.Y_AXIS));
        recipesPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        recipesScrollPane.setViewportView(recipesPanel);
        recipesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        recipesScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        recipeSearchField.addCaretListener(event -> updateRecipePanelsVisibility());

        recipeNewButton.addActionListener(event -> {
            new RecipeUpdateDialog(null);
            refresh();
        });
    }

    public void populateRecipeTab() {
        recipesPanel.removeAll();

        Recipe[] recipes = new Recipe().select();
        if (recipes == null) {
            return;
        }

        recipePanels = new RecipePanel[recipes.length];
        for (int index = 0; index < recipes.length; index++) {
            Recipe recipe = recipes[index];
            RecipePanel recipePanel = new RecipePanel(recipe);
            recipePanels[index] = recipePanel;
            recipesPanel.add(recipePanel.contentPane);
        }

        updateRecipePanelsVisibility();
    }

    public void updateRecipePanelsVisibility() {
        String searchTerm = recipeSearchField.getText().toLowerCase();

        for (RecipePanel recipePanel : recipePanels) {
            if (!searchTerm.isEmpty()) {
                Recipe recipe = recipePanel.recipe;

                boolean matches = false;
                if (recipe.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                } else if (recipe.category.toLowerCase().contains(searchTerm)) {
                    matches = true;
                } else {
                    for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
                        FoodItem foodItem = recipeIngredient.getFoodItem();

                        if (foodItem.name.toLowerCase().contains(searchTerm)) {
                            matches = true;
                            break;
                        }
                    }
                }
                if (!matches) {
                    recipePanel.contentPane.setVisible(false);
                    continue;
                }
            }

            recipePanel.contentPane.setVisible(true);

        }
    }

    public void setupMealPlansTab() {
        mealPlansPanel = new JPanel();
        mealPlansPanel.setLayout(new BoxLayout(mealPlansPanel, BoxLayout.Y_AXIS));
        mealPlansPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        mealPlansScrollPane.setViewportView(mealPlansPanel);
        mealPlansScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mealPlansScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        mealPlanSearchField.addCaretListener(event -> updateMealPlanPanelsVisibility());
        mealPlanShowPastCheckBox.addItemListener(event -> updateMealPlanPanelsVisibility());

        mealPlanNewButton.addActionListener(event -> {
            new MealPlanUpdateDialog(null);
            refresh();
        });
    }

    public void populateMealPlansTab() {
        mealPlansPanel.removeAll();

        MealPlan[] mealPlans = new MealPlan().select();
        if (mealPlans == null) {
            return;
        }

        mealPlanPanels = new MealPlanPanel[mealPlans.length];
        for (int index = 0; index < mealPlans.length; index++) {
            MealPlan mealPlan = mealPlans[index];
            MealPlanPanel mealPlanPanel = new MealPlanPanel(mealPlan);
            mealPlanPanels[index] = mealPlanPanel;
            mealPlansPanel.add(mealPlanPanel.contentPane);
        }

        updateMealPlanPanelsVisibility();
    }

    public void updateMealPlanPanelsVisibility() {
        String searchTerm = mealPlanSearchField.getText().toLowerCase();

        boolean showPast = mealPlanShowPastCheckBox.isSelected();
        Date today = new Date(System.currentTimeMillis());

        for (MealPlanPanel mealPlanPanel : mealPlanPanels) {
            if (!searchTerm.isEmpty()) {
                MealPlan mealPlan = mealPlanPanel.mealPlan;

                boolean matches = false;
                if (mealPlan.week_start.toString().toLowerCase().contains(searchTerm)) {
                    matches = true;
                } else if (mealPlan.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                } else {
                    for (Meal meal : mealPlan.getMeals()) {
                        Recipe recipe = meal.getRecipe();

                        if (recipe.name.toLowerCase().contains(searchTerm)) {
                            matches = true;
                            break;
                        }
                    }
                }
                if (!matches) {
                    mealPlanPanel.contentPane.setVisible(false);
                    continue;
                }
            }

            if (mealPlanPanel.mealPlan.getWeekEnd().before(today)) {
                mealPlanPanel.contentPane.setVisible(showPast);
                continue;
            }

            mealPlanPanel.contentPane.setVisible(true);
        }
    }

    public void setupFridgeTab() {
        fridgeItemsPanel = new JPanel();
        fridgeItemsPanel.setLayout(new BoxLayout(fridgeItemsPanel, BoxLayout.Y_AXIS));
        fridgeItemsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        fridgeScrollPane.setViewportView(fridgeItemsPanel);
        fridgeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        fridgeScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        fridgeSearchField.addCaretListener(event -> updateFridgeItemPanelsVisibility());

        fridgeNewButton.addActionListener(event -> {
            new FridgeItemUpdateDialog(null);
            refresh();
        });
    }

    public void populateFridgeTab() {
        fridgeItemsPanel.removeAll();

        FridgeItem[] fridgeItems = new FridgeItem().select();
        if (fridgeItems == null) {
            return;
        }

        fridgeItemPanels = new FridgeItemPanel[fridgeItems.length];
        for (int index = 0; index < fridgeItems.length; index++) {
            FridgeItem fridgeItem = fridgeItems[index];
            FridgeItemPanel fridgeItemPanel = new FridgeItemPanel(fridgeItem);
            fridgeItemPanels[index] = fridgeItemPanel;
            fridgeItemsPanel.add(fridgeItemPanel.contentPane);
        }

        updateFridgeItemPanelsVisibility();
    }

    public void updateFridgeItemPanelsVisibility() {
        String searchTerm = fridgeSearchField.getText().toLowerCase();

        for (FridgeItemPanel fridgeItemPanel : fridgeItemPanels) {
            if (!searchTerm.isEmpty()) {
                FridgeItem fridgeItem = fridgeItemPanel.fridgeItem;
                FoodItem foodItem = fridgeItem.getFoodItem();

                boolean matches = false;
                if (foodItem.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                }
                if (!matches) {
                    fridgeItemPanel.contentPane.setVisible(false);
                    continue;
                }
            }

            fridgeItemPanel.contentPane.setVisible(true);
        }
    }

    public void setupShoppingListTab() {
        shoppingItemsPanel = new JPanel();
        shoppingItemsPanel.setLayout(new BoxLayout(shoppingItemsPanel, BoxLayout.Y_AXIS));
        shoppingItemsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        shoppingScrollPane.setViewportView(shoppingItemsPanel);
        shoppingScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        shoppingScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        shoppingSearchField.addCaretListener(event -> updateShoppingItemPanelsVisibility());
    }

    public void populateShoppingListTab() {
        shoppingItemsPanel.removeAll();

        ShoppingListItem[] shoppingListItems = new ShoppingListItem().select();
        if (shoppingListItems == null) {
            return;
        }

        shoppingItemPanels = new ShoppingItemPanel[shoppingListItems.length];
        for (int index = 0; index < shoppingListItems.length; index++) {
            ShoppingListItem shoppingListItem = shoppingListItems[index];
            // TODO: segment this by weeks?
            ShoppingItemPanel shoppingItemPanel = new ShoppingItemPanel(shoppingListItem);
            shoppingItemPanels[index] = shoppingItemPanel;
            this.shoppingItemsPanel.add(shoppingItemPanel.contentPane);
        }

        updateShoppingItemPanelsVisibility();
    }

    public void updateShoppingItemPanelsVisibility() {
        String searchTerm = shoppingSearchField.getText().toLowerCase();

        for (ShoppingItemPanel shoppingItemPanel : shoppingItemPanels) {
            if (!searchTerm.isEmpty()) {
                ShoppingListItem shoppingListItem = shoppingItemPanel.shoppingListItem;
                FoodItem foodItem = shoppingListItem.getFoodItem();

                boolean matches = false;
                if (foodItem.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                }
                if (!matches) {
                    shoppingItemPanel.contentPane.setVisible(false);
                    continue;
                }
            }

            shoppingItemPanel.contentPane.setVisible(true);
        }
    }

    private PlaceholderTextField createSearchField(String placeholder) {
        PlaceholderTextField searchField = new PlaceholderTextField();
        searchField.setPlaceholder(placeholder);
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setMinimumSize(new Dimension(300, 30));
        searchField.setMaximumSize(new Dimension(300, 30));
        searchField.setColumns(10);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchField.setOpaque(false);
        searchField.setMargin(new Insets(5, 5, 5, 5));
        searchField.setFocusable(true);
        return searchField;
    }

    /**
     * Used by IntelliJ IDEA GUI Designer, for custom components
     */
    public void createUIComponents() {
        recipeSearchField = createSearchField("Search for a recipe...");
        mealPlanSearchField = createSearchField("Search for a meal plan...");
        fridgeSearchField = createSearchField("Search for an item...");
        shoppingSearchField = createSearchField("Search for an item...");
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
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel2.add(panel3, BorderLayout.EAST);
        recipeNewButton = new JButton();
        recipeNewButton.setText("New Recipe");
        panel3.add(recipeNewButton);
        recipesScrollPane = new JScrollPane();
        panel1.add(recipesScrollPane, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Meal Plans", panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel4.add(panel5, BorderLayout.NORTH);
        panel5.add(mealPlanSearchField, BorderLayout.CENTER);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel5.add(panel6, BorderLayout.EAST);
        mealPlanShowPastCheckBox = new JCheckBox();
        mealPlanShowPastCheckBox.setText("Show Past");
        panel6.add(mealPlanShowPastCheckBox);
        final JSeparator separator1 = new JSeparator();
        separator1.setPreferredSize(new Dimension(10, 0));
        panel6.add(separator1);
        mealPlanNewButton = new JButton();
        mealPlanNewButton.setText("New Meal Plan");
        panel6.add(mealPlanNewButton);
        mealPlansScrollPane = new JScrollPane();
        panel4.add(mealPlansScrollPane, BorderLayout.CENTER);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Fridge/Pantry", panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        panel7.add(panel8, BorderLayout.NORTH);
        panel8.add(fridgeSearchField, BorderLayout.CENTER);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel8.add(panel9, BorderLayout.EAST);
        fridgeNewButton = new JButton();
        fridgeNewButton.setLabel("New Item");
        fridgeNewButton.setText("New Item");
        panel9.add(fridgeNewButton);
        fridgeScrollPane = new JScrollPane();
        panel7.add(fridgeScrollPane, BorderLayout.CENTER);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Shopping List", panel10);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new BorderLayout(0, 0));
        panel10.add(panel11, BorderLayout.NORTH);
        panel11.add(shoppingSearchField, BorderLayout.CENTER);
        shoppingScrollPane = new JScrollPane();
        panel10.add(shoppingScrollPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
