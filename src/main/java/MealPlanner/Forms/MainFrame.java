package MealPlanner.Forms;

import MealPlanner.DatabaseHelper;
import MealPlanner.Forms.Components.PlaceholderTextField;
import MealPlanner.Models.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static MealPlanner.Main.*;

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
    public JPanel fridgeItemsPanel;
    public JPanel shoppingItemsPanel;
    private RecipePanel[] recipePanels;
    private FridgeItemPanel[] fridgeItemPanels;
    private ShoppingItemPanel[] shoppingItemPanels;

    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setTitle("Meal Planner");
        setMinimumSize(new Dimension(400, 300));
        setSize(new Dimension(800, 600));
        setLocationByPlatform(true);

        $$$setupUI$$$();

        tabbedPane.setSelectedIndex(-1);

        setupRecipeTab();
        setupMealPlansTab();
        setupFridgeTab();
        setupShoppingListTab();

        setContentPane(contentPane);
        setVisible(true);

        initialize();

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
        tabbedPane.setSelectedIndex(0);
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

        recipeSearchField.addCaretListener(event -> {
            String searchTerm = recipeSearchField.getText().toLowerCase();

            for (RecipePanel recipePanel : recipePanels) {
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
                recipePanel.contentPane.setVisible(matches);
            }
        });

        recipeNewButton.addActionListener(event -> {
            // TODO
        });
    }

    public void populateRecipeTab() {
        recipesPanel.removeAll();

        Recipe[] recipes = new Recipe().select();
        if (recipes == null) {
            return;
        }

        recipePanels = new RecipePanel[recipes.length];
        recipeSearchField.setText("");
        for (int index = 0; index < recipes.length; index++) {
            Recipe recipe = recipes[index];
            RecipePanel recipePanel = new RecipePanel(recipe);
            recipePanels[index] = recipePanel;
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
        fridgeItemsPanel = new JPanel();
        fridgeItemsPanel.setLayout(new BoxLayout(fridgeItemsPanel, BoxLayout.Y_AXIS));
        fridgeItemsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        fridgeScrollPane.setViewportView(fridgeItemsPanel);

        fridgeSearchField.addCaretListener(event -> {
            String searchTerm = fridgeSearchField.getText().toLowerCase();

            for (FridgeItemPanel fridgeItemPanel : fridgeItemPanels) {
                FridgeItem fridgeItem = fridgeItemPanel.fridgeItem;
                FoodItem foodItem = fridgeItem.getFoodItem();

                boolean matches = false;
                if (foodItem.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                }
                fridgeItemPanel.contentPane.setVisible(matches);
            }
        });

        fridgeNewButton.addActionListener(event -> {
            // TODO
        });
    }

    public void populateFridgeTab() {
        fridgeItemsPanel.removeAll();

        FridgeItem[] fridgeItems = new FridgeItem().select();
        if (fridgeItems == null) {
            return;
        }

        fridgeItemPanels = new FridgeItemPanel[fridgeItems.length];
        fridgeSearchField.setText("");
        for (int index = 0; index < fridgeItems.length; index++) {
            FridgeItem fridgeItem = fridgeItems[index];
            FridgeItemPanel fridgeItemPanel = new FridgeItemPanel(fridgeItem);
            fridgeItemPanels[index] = fridgeItemPanel;
            fridgeItemsPanel.add(fridgeItemPanel.contentPane);
        }
    }

    public void setupShoppingListTab() {
        shoppingItemsPanel = new JPanel();
        shoppingItemsPanel.setLayout(new BoxLayout(shoppingItemsPanel, BoxLayout.Y_AXIS));
        shoppingItemsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        shoppingScrollPane.setViewportView(shoppingItemsPanel);

        shoppingSearchField.addCaretListener(event -> {
            String searchTerm = shoppingSearchField.getText().toLowerCase();

            for (ShoppingItemPanel shoppingItemPanel : shoppingItemPanels) {
                ShoppingListItem shoppingListItem = shoppingItemPanel.shoppingListItem;

                boolean matches = false;
                if (shoppingListItem.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                }
                shoppingItemPanel.contentPane.setVisible(matches);
            }
        });
    }

    public void populateShoppingListTab() {
        shoppingItemsPanel.removeAll();

        ShoppingListItem[] shoppingListItems = new ShoppingListItem().select();
        if (shoppingListItems == null) {
            return;
        }

        shoppingItemPanels = new ShoppingItemPanel[shoppingListItems.length];
        shoppingSearchField.setText("");
        for (int index = 0; index < shoppingListItems.length; index++) {
            ShoppingListItem shoppingListItem = shoppingListItems[index];
            // TODO: segment this by weeks?
            ShoppingItemPanel shoppingItemPanel = new ShoppingItemPanel(shoppingListItem);
            shoppingItemPanels[index] = shoppingItemPanel;
            this.shoppingItemsPanel.add(shoppingItemPanel.contentPane);
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
        recipesScrollPane.setHorizontalScrollBarPolicy(30);
        panel1.add(recipesScrollPane, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Meal Plans", panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Fridge/Pantry", panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel5.add(panel6, BorderLayout.NORTH);
        panel6.add(fridgeSearchField, BorderLayout.CENTER);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel6.add(panel7, BorderLayout.EAST);
        fridgeNewButton = new JButton();
        fridgeNewButton.setLabel("New Item");
        fridgeNewButton.setText("New Item");
        panel7.add(fridgeNewButton);
        fridgeScrollPane = new JScrollPane();
        panel5.add(fridgeScrollPane, BorderLayout.CENTER);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Shopping List", panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel8.add(panel9, BorderLayout.NORTH);
        panel9.add(shoppingSearchField, BorderLayout.CENTER);
        shoppingScrollPane = new JScrollPane();
        panel8.add(shoppingScrollPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
