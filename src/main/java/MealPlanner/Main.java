package MealPlanner;

import MealPlanner.Forms.MainFrame;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {
    public static MainFrame mainFrame;
    public static JDialog dialog;
    public static JOptionPane dialogPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            mainFrame = new MainFrame();
        });
        // TODO: move and remove the rest of this function once everything's implemented

        /*DatabaseHelper.connect();
        DatabaseHelper.setup();

        // testing selection
        FoodItem foodItemCriteria = new FoodItem();
        FoodItem[] foodItems = foodItemCriteria.select();
        if (foodItems != null) {
            StringBuilder foodItemBuilder = new StringBuilder();
            for (FoodItem foodItem : foodItems) {
                foodItemBuilder.append("ID: %s, Name: %s, Unit: %s, Group: %s\n".formatted(
                        foodItem.id,
                        foodItem.name,
                        foodItem.unit,
                        foodItem.food_group));
            }
            displayInfoDialog(foodItemBuilder.toString());
        }

        // testing WHERE selection
        foodItemCriteria = new FoodItem();
        foodItemCriteria.name = "White Sandwich Bread";
        foodItems = foodItemCriteria.select();
        if (foodItems != null) {
            StringBuilder foodItemBuilder = new StringBuilder();
            for (FoodItem foodItem : foodItems) {
                foodItemBuilder.append("ID: %s, Name: %s, Unit: %s, Group: %s\n".formatted(
                        foodItem.id,
                        foodItem.name,
                        foodItem.unit,
                        foodItem.food_group));
            }
            displayInfoDialog(foodItemBuilder.toString());
        }

        // testing WHERE LIKE selection
        foodItemCriteria.name = null; // remove name from search
        foodItemCriteria.unit = "%spoon";
        foodItems = foodItemCriteria.select();
        if (foodItems != null) {
            StringBuilder foodItemBuilder = new StringBuilder();
            for (FoodItem foodItem : foodItems) {
                foodItemBuilder.append("ID: %s, Name: %s, Unit: %s, Group: %s\n".formatted(
                        foodItem.id,
                        foodItem.name,
                        foodItem.unit,
                        foodItem.food_group));
            }
            displayInfoDialog(foodItemBuilder.toString());
        }

        // testing insert
        FoodItem foodItemTest = new FoodItem();
        foodItemTest.name = "White Sandwich Bread";
        foodItemTest.unit = "slice";
        if (foodItemTest.insert()) {
            displayInfoDialog("inserted %s", foodItemTest.id);
        }

        // testing update
        foodItemTest.food_group = "grains";
        if (foodItemTest.update()) {
            displayInfoDialog("updated %s", foodItemTest.id);
        }

        // testing delete
        if (foodItemTest.delete()) {
            displayInfoDialog("deleted %s", foodItemTest.id);
        }

        DatabaseHelper.disconnect();
        System.exit(0);*/
    }

    /**
     * Converts the output of {@link Exception#printStackTrace()} to a {@link String} and returns it
     *
     * @param exception Exception to get the stack trace of
     * @return Exception stack trace
     */
    private static String getStackTrace(Exception exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }

    /**
     * Displays a message dialog using the specified title, message type, and formatted message
     * <p>
     * Exceptions passed as format parameters will be converted to stack traces using {@link #getStackTrace(Exception)}
     * <p>
     * The created {@link JDialog} and {@link JOptionPane} can be accessed via {@link #dialog} and {@link #dialogPane} respectively
     *
     * @param title      The title of the message dialog
     * @param type       The type of the message (e.g., JOptionPane constants such as JOptionPane.INFORMATION_MESSAGE)
     * @param message    The message to display, which can include format specifiers
     * @param parameters Optional; parameters used to format the message
     */
    public static void createOrUpdateDialog(String title, Integer type, Object[] options, String message, Object... parameters) {
        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index] instanceof Exception) {
                parameters[index] = getStackTrace((Exception) parameters[index]);
            }
        }

        if (dialog != null) {
            if (title != null) {
                dialog.setTitle(title);
            }
            if (type != null) {
                dialogPane.setMessageType(type);
            }
            if (message != null) {
                dialogPane.setMessage(message.formatted(parameters));
            }
            if (options != null) {
                dialogPane.setOptions(options);
            }
            dialog.pack();
            return;
        }

        dialogPane = new JOptionPane(message.formatted(parameters), type, JOptionPane.DEFAULT_OPTION);
        dialog = dialogPane.createDialog(mainFrame, title);
        dialog.pack();
    }

    /**
     * Using {@code "Information"} as the title and {@code JOptionPane.INFORMATION_MESSAGE} as the type, passes message
     * and optional format parameters to {@link #createOrUpdateDialog(String, Integer, Object[], String, Object...)}
     */
    public static void createInfoDialog(String message, Object... parameters) {
        createOrUpdateDialog("Information", JOptionPane.INFORMATION_MESSAGE, new Object[]{"OK"}, message, parameters);
    }

    /**
     * Passes message and optional format parameters to {@link #createInfoDialog(String, Object...)}
     */
    public static void displayInfoDialog(String message, Object... parameters) {
        createInfoDialog(message, parameters);
        dialog.setVisible(true);
    }

    /**
     * Using {@code "ERROR"} as the title and {@code JOptionPane.ERROR_MESSAGE} as the type, passes message
     * and optional format parameters to {@link #createOrUpdateDialog(String, Integer, Object[], String, Object...)}
     */
    public static void createErrorDialog(String message, Object... parameters) {
        createOrUpdateDialog("ERROR", JOptionPane.ERROR_MESSAGE, new Object[]{"OK"}, message, parameters);
    }

    /**
     * Passes message and optional format parameters to {@link #createErrorDialog(String, Object...)}
     */
    public static void displayErrorDialog(String message, Object... parameters) {
        createErrorDialog(message, parameters);
        dialog.setVisible(true);
    }
}