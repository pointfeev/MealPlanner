package MealPlanner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Connecting to the database...");
        if (!DatabaseHelper.connect()) {
            return;
        }
        System.out.println("Setting up the database...");
        if (!DatabaseHelper.setup()) {
            return;
        }
        System.out.println("Connected successfully, running a query...");
        try {
            ArrayList<HashMap<String, Object>> results;
            try {
                results = DatabaseHelper.executeQuery("SELECT table_name FROM all_tables WHERE owner = SYS_CONTEXT('USERENV', 'CURRENT_USER')");
            } catch (SQLException exception) {
                System.out.println("Query failed!");
                return;
            }
            for (HashMap<String, Object> result : results) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
                }
            }
            System.out.println("Query successful!");
        } finally {
            DatabaseHelper.disconnect();
            System.out.println("Disconnected from the database.");
        }
    }

    /**
     * Converts the output of {@link Exception#printStackTrace()} to a {@link String} and sends it to {@link #outputException(String)}
     *
     * @param exception Exception to output the stack trace for
     */
    public static void outputException(Exception exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        outputException(errors.toString());
    }

    /**
     * TODO: For now, sends the {@code message} to {@code System.out.println}; later, this will display a dialog with the {@code message}
     * <p>
     * TODO: This method and {@link #outputException(Exception)} will probably be moved to the class that handles the UI once that is developed
     *
     * @param message Message to output
     */
    public static void outputException(String message) {
        System.out.println(message);
        // JOptionPane.showMessageDialog(null, e);
    }
}