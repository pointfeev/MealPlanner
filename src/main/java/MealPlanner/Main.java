package MealPlanner;

import MealPlanner.GUI.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static MainFrame mainFrame;
    public static JDialog dialog;
    public static JOptionPane dialogPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            initialize();
            mainFrame = new MainFrame();
        });
    }

    public static void initialize() {
        createOrUpdateDialog("Meal Planner", JOptionPane.INFORMATION_MESSAGE, new Object[]{"Cancel"}, "Initializing...");

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
            dialog.setVisible(false);
        }).start();

        dialog.setVisible(true);
        if (!success.get() && dialogPane.getValue() != null) {
            System.exit(0);
        }
    }

    public static Window getCurrentWindow() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window.isActive()) {
                return window;
            }
        }
        for (Window window : windows) {
            if (window.isVisible()) {
                return window;
            }
        }
        return mainFrame;
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

            if (!dialog.isVisible()) {
                dialog.setLocationRelativeTo(getCurrentWindow());
            }
            return;
        }

        dialogPane = new JOptionPane(message.formatted(parameters), type, JOptionPane.DEFAULT_OPTION, null, options, null);
        dialog = dialogPane.createDialog(getCurrentWindow(), title);
    }

    /**
     * Using {@code "Information"} as the title and {@code JOptionPane.INFORMATION_MESSAGE} as the type, passes message
     * and optional format parameters to {@link #createOrUpdateDialog(String, Integer, Object[], String, Object...)}
     */
    public static void createInfoDialog(String message, Object... parameters) {
        createOrUpdateDialog("Meal Planner - Information", JOptionPane.INFORMATION_MESSAGE, new Object[]{"OK"}, message, parameters);
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
        createOrUpdateDialog("Meal Planner - ERROR", JOptionPane.ERROR_MESSAGE, new Object[]{"OK"}, message, parameters);
    }

    /**
     * Passes message and optional format parameters to {@link #createErrorDialog(String, Object...)}
     */
    public static void displayErrorDialog(String message, Object... parameters) {
        createErrorDialog(message, parameters);
        dialog.setVisible(true);
    }
}