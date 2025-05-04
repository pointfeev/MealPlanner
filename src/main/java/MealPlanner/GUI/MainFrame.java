package MealPlanner.GUI;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainFrame extends javax.swing.JFrame {
    public static MainFrame frame;

    public MainFrame() {
        this.setTitle("Meal Planner");
        this.setSize(800, 600);
        this.setLocationByPlatform(true);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // TODO
    }

    public static MainFrame get() {
        if (frame == null) {
            frame = new MainFrame();
            frame.setVisible(true);
        }
        return frame;
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
     *
     * @param title      The title of the message dialog
     * @param type       The type of the message (e.g., JOptionPane constants such as JOptionPane.INFORMATION_MESSAGE)
     * @param message    The message to display, which can include format specifiers
     * @param parameters Optional; parameters used to format the message
     */
    public static void displayDialog(String title, int type, String message, Object... parameters) {
        for (int index = 0; index < parameters.length; index++) {
            if (parameters[index] instanceof Exception) {
                parameters[index] = getStackTrace((Exception) parameters[index]);
            }
        }
        JOptionPane.showMessageDialog(get(), message.formatted(parameters), title, type);
    }

    /**
     * Using {@code "Information"} as the title and {@code JOptionPane.INFORMATION_MESSAGE} as the type, passes message
     * and optional format parameters to {@link #displayDialog(String, int, String, Object...)}
     */
    public static void displayInfoDialog(String message, Object... parameters) {
        displayDialog("Information", JOptionPane.INFORMATION_MESSAGE, message, parameters);
    }

    /**
     * Using {@code "ERROR"} as the title and {@code JOptionPane.ERROR_MESSAGE} as the type, passes message
     * and optional format parameters to {@link #displayDialog(String, int, String, Object...)}
     */
    public static void displayErrorDialog(String message, Object... parameters) {
        displayDialog("ERROR", JOptionPane.ERROR_MESSAGE, message, parameters);
    }
}
