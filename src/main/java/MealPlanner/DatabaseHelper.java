package MealPlanner;

import MealPlanner.Models.*;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.driver.OracleDriver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static MealPlanner.Main.displayErrorDialog;

public class DatabaseHelper {
    private final static int LOGIN_TIMEOUT_SECONDS = 3;

    private static Connection connection = null;

    /**
     * Establishes a JDBC connection with the database; see {@link DriverManager#getConnection(String)}
     */
    public static boolean connect() {
        Properties properties = getProperties();
        if (properties == null) {
            return false;
        }
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        try {
            DriverManager.registerDriver(new OracleDriver()); // optional, but just in case

            DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
            connection = DriverManager.getConnection(url, username, password);

            return true;
        } catch (SQLTimeoutException exception) {
            displayErrorDialog("Failed to connect to the database!\n\nConnection timed out after %d seconds!", LOGIN_TIMEOUT_SECONDS);
        } catch (SQLException exception) {
            displayErrorDialog("Encountered an error while connecting to the database!\n\n%s", exception);
        }
        return false;
    }

    /**
     * Closes the JDBC connection to the database; see {@link Connection#close()}
     */
    public static void disconnect() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException exception) {
            displayErrorDialog("Encountered an error while disconnecting from the database!\n\n%s", exception);
        }
        connection = null;
    }

    /**
     * Checks if any of the model tables do not exist by querying the {@code all_tables} view, and if one does not exist,
     * runs all the statements in the setup SQL script file {@code database.sql}.
     */
    public static boolean setup() {
        HashSet<String> tables = new HashSet<>();
        try (OraclePreparedStatement statement = prepareStatement(
                "SELECT table_name FROM all_tables WHERE owner = SYS_CONTEXT('USERENV', 'CURRENT_USER')")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tables.add(resultSet.getString(1));
                }
            }
        } catch (SQLException exception) {
            displayErrorDialog("Failed to query the database during setup!\n\n%s", exception.getMessage());
            return false;
        }

        if (!tables.contains(FoodItem.TABLE)
                || !tables.contains(FridgeItem.TABLE)
                || !tables.contains(Meal.TABLE)
                || !tables.contains(MealPlan.TABLE)
                || !tables.contains(Recipe.TABLE)
                || !tables.contains(RecipeIngredient.TABLE)
                || !tables.contains(RecipeInstruction.TABLE)) {
            try (InputStream stream = DatabaseHelper.class.getClassLoader().getResourceAsStream("database.sql")) {
                if (stream == null) {
                    displayErrorDialog("Failed to read the database setup SQL file!");
                    return false;
                }

                for (String sql : new String(stream.readAllBytes()).split("/")) {
                    sql = sql.trim();
                    try {
                        try (OraclePreparedStatement statement = prepareStatement(sql)) {
                            statement.executeUpdate();
                        }
                    } catch (SQLException exception) {
                        displayErrorDialog("Encountered an error while setting up the database!\n\n%s\n\n%s", sql, exception.getMessage());
                        return false;
                    }
                }
            } catch (IOException exception) {
                displayErrorDialog("Encountered an error while setting up the database!\n\n%s", exception);
                return false;
            }
        }
        return true;
    }

    /**
     * Helper recursive method for {@link #prepareStatement(String, Object...)}
     */
    private static void setParameters(OraclePreparedStatement statement, AtomicInteger parameterIndex, Object[] parameters) throws SQLException {
        for (Object parameter : parameters) {
            if (parameter instanceof Object[]) {
                setParameters(statement, parameterIndex, (Object[]) parameter);
                continue;
            }
            statement.setObject(parameterIndex.getAndIncrement(), parameter);
        }
    }

    /**
     * @param sql         SQL statement to prepare; see {@link Connection#prepareStatement(String, String[])}
     * @param columnNames Column names to return from the inserted row(s); see {@link Connection#prepareStatement(String, String[])}
     * @param parameters  Parameters to bind to the SQL statement (if any); see {@link OraclePreparedStatement#setObject(int, Object)}
     * @return The prepared statement with parameters (if any) bound to it; see {@link Connection#prepareStatement(String, String[])} and {@link OraclePreparedStatement#setObject(int, Object)}
     * @throws SQLException May be thrown be either {@link Connection#prepareStatement(String)} or {@link OraclePreparedStatement#setObject(int, Object)}
     */
    public static OraclePreparedStatement prepareStatement(String sql, String[] columnNames, Object... parameters) throws SQLException {
        OraclePreparedStatement statement;
        if (columnNames == null || columnNames.length == 0) {
            statement = (OraclePreparedStatement) connection.prepareStatement(sql);
        } else {
            statement = (OraclePreparedStatement) connection.prepareStatement(sql, columnNames);
        }
        setParameters(statement, new AtomicInteger(1), parameters);
        return statement;
    }

    /**
     * @param sql        SQL statement to prepare; see {@link Connection#prepareStatement(String)}
     * @param parameters Parameters to bind to the SQL statement (if any); see {@link OraclePreparedStatement#setObject(int, Object)}
     * @return The prepared statement with parameters (if any) bound to it; see {@link Connection#prepareStatement(String, String[])} and {@link OraclePreparedStatement#setObject(int, Object)}
     * @throws SQLException May be thrown be either {@link Connection#prepareStatement(String)} or {@link OraclePreparedStatement#setObject(int, Object)}
     */
    public static OraclePreparedStatement prepareStatement(String sql, Object... parameters) throws SQLException {
        return prepareStatement(sql, null, parameters);
    }

    /**
     * @return If an error occurred while reading the {@code database.properties} resource file, {@code null};
     * otherwise, a {@link Properties} object containing the properties in the {@code database.properties} resource file
     * (see {@link ClassLoader#getResourceAsStream(String)} and {@link Properties#load(InputStream)})
     */
    private static Properties getProperties() {
        try (InputStream stream = DatabaseHelper.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (stream == null) {
                displayErrorDialog("Failed to read the database properties file!\n\nMake sure to follow the instructions in the README!");
                return null;
            }

            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException exception) {
            displayErrorDialog("Encountered an error while getting database properties!\n\n%s", exception);
        }
        return null;
    }
}
