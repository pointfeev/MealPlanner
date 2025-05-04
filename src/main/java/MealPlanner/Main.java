package MealPlanner;

import MealPlanner.GUI.MainFrame;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static MainFrame mainFrame;

    public static void main(String[] args) {
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // TODO: move and remove all of this once the UI is set up
        StringBuilder testString = new StringBuilder();
        testString.append("Connecting to the database...\n");
        if (!DatabaseHelper.connect()) {
            return;
        }
        testString.append("Setting up the database...\n");
        if (!DatabaseHelper.setup()) {
            return;
        }
        testString.append("Connected successfully, running a query...\n");
        try {
            ArrayList<HashMap<String, Object>> results;
            try {
                results = DatabaseHelper.executeQuery("SELECT table_name FROM all_tables WHERE owner = SYS_CONTEXT('USERENV', 'CURRENT_USER')");
            } catch (SQLException exception) {
                testString.append("Query failed!\n");
                return;
            }
            for (HashMap<String, Object> result : results) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    testString.append("%s: %s\n".formatted(entry.getKey(), entry.getValue()));
                }
            }
            testString.append("Query successful!\n");
        } finally {
            DatabaseHelper.disconnect();
            testString.append("Disconnected from the database.\n");
        }
        displayDialog("Database Test", JOptionPane.INFORMATION_MESSAGE, testString.toString());
    }

    /**
     * Converts the output of {@link Exception#printStackTrace()} to a {@link String} and returns it
     *
     * @param exception Exception to get the stack trace of
     * @return Exception stack trace
     */
    public static String getStackTrace(Exception exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    /**
     * Displays a message dialog using the specified title, message type, and formatted message
     *
     * @param title      The title of the message dialog
     * @param type       The type of the message (e.g., JOptionPane constants such as JOptionPane.INFORMATION_MESSAGE)
     * @param message    The message to display, which can include format specifiers
     * @param parameters Optional; parameters used to format the message
     */
    public static void displayDialog(String title, int type, String message, Object... parameters) {
        JOptionPane.showMessageDialog(mainFrame, message.formatted(parameters), title, type);
    }

    /**
     * Displays an error dialog with the specified message and formatted parameters
     *
     * @param message    The error message to display, which can include format specifiers
     * @param parameters Optional; parameters used to format the message.
     */
    public static void displayErrorDialog(String message, Object... parameters) {
        JOptionPane.showMessageDialog(mainFrame, message.formatted(parameters), "ERROR", JOptionPane.ERROR_MESSAGE);
    }
}