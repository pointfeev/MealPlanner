package MealPlanner.Forms;

import MealPlanner.DatabaseHelper;
import MealPlanner.Forms.Components.PlaceholderTextField;
import MealPlanner.Models.Recipe;
import MealPlanner.Models.RecipeIngredient;

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
    private RecipePanel[] recipePanels;

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
            for (RecipePanel recipePanel : recipePanels) {
                boolean matches = false;
                String searchTerm = recipeSearchField.getText().toLowerCase();
                if (recipePanel.recipe.name.toLowerCase().contains(searchTerm)) {
                    matches = true;
                } else if (recipePanel.recipe.category.toLowerCase().contains(searchTerm)) {
                    matches = true;
                } else {
                    for (RecipeIngredient recipeIngredient : recipePanel.recipe.getIngredients()) {
                        if (recipeIngredient.getFoodItem().name.toLowerCase().contains(searchTerm)) {
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
        panel5.setLayout(new GridBagLayout());
        tabbedPane.addTab("Fridge", panel5);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        tabbedPane.addTab("Shopping List", panel6);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
