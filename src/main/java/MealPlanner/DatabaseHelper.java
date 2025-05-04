package MealPlanner;

import MealPlanner.Models.*;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.driver.OracleDriver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import static MealPlanner.Main.*;

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
            displayErrorDialog("Encountered an error while connecting to the database!\n\n%s", getStackTrace(exception));
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
            displayErrorDialog("Encountered an error while disconnecting from the database!\n\n%s", getStackTrace(exception));
        }
        connection = null;
    }

    /**
     * Helper method for {@link #setup()}
     */
    private static boolean tableDoesNotExist(ArrayList<HashMap<String, Object>> tables, String tableName) {
        return tables.stream().noneMatch(table -> table.get("TABLE_NAME").equals(tableName));
    }

    /**
     * Checks if any of the model tables do not exist by querying the {@code all_tables} view, and if one does not exist,
     * runs all the statements in the setup SQL script file {@code database.sql}.
     */
    public static boolean setup() {
        ArrayList<HashMap<String, Object>> tables;
        try {
            tables = DatabaseHelper.executeQuery("SELECT table_name FROM all_tables WHERE owner = SYS_CONTEXT('USERENV', 'CURRENT_USER')");
        } catch (SQLException exception) {
            displayErrorDialog("Failed to query the database during setup!\n\n%s", exception.getMessage());
            return false;
        }

        if (tableDoesNotExist(tables, FoodItem.TABLE)
                || tableDoesNotExist(tables, FridgeItem.TABLE)
                || tableDoesNotExist(tables, Meal.TABLE)
                || tableDoesNotExist(tables, MealPlan.TABLE)
                || tableDoesNotExist(tables, Recipe.TABLE)
                || tableDoesNotExist(tables, RecipeIngredient.TABLE)
                || tableDoesNotExist(tables, RecipeInstruction.TABLE)) {
            try (InputStream stream = DatabaseHelper.class.getClassLoader().getResourceAsStream("database.sql")) {
                if (stream == null) {
                    displayErrorDialog("Failed to read the database setup SQL file!");
                    return false;
                }

                for (String sql : new String(stream.readAllBytes()).split("/")) {
                    sql = sql.trim();
                    try {
                        DatabaseHelper.executeUpdate(sql);
                    } catch (SQLException exception) {
                        displayErrorDialog("Encountered an error while setting up the database!\n\n%s\n\n%s", sql, exception.getMessage());
                        return false;
                    }
                }
            } catch (IOException exception) {
                displayErrorDialog("Encountered an error while setting up the database!\n\n%s", getStackTrace(exception));
                return false;
            }
        }
        return true;
    }

    /**
     * @param sql        SQL statement to prepare; see {@link Connection#prepareStatement(String)}
     * @param parameters Parameters to bind to the SQL statement (if any); see {@link OraclePreparedStatement#setObject(int, Object)}
     * @return The prepared statement with parameters (if any) bound to it; see {@link Connection#prepareStatement(String)} and {@link OraclePreparedStatement#setObject(int, Object)}
     * @throws SQLException May be thrown be either {@link Connection#prepareStatement(String)} or {@link OraclePreparedStatement#setObject(int, Object)}
     */
    public static OraclePreparedStatement prepareStatement(String sql, Object... parameters) throws SQLException {
        OraclePreparedStatement statement = (OraclePreparedStatement) connection.prepareStatement(sql);
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index, parameters[index]);
        }
        return statement;
    }

    /**
     * @param sql        SQL statement to prepare and execute; see {@link #prepareStatement(String, Object...)}
     * @param parameters Parameters to bind to the SQL statement (if any); see {@link #prepareStatement(String, Object...)}
     * @return An array of hash maps,
     * with each hash map corresponding to a row in the result set (see {@link OraclePreparedStatement#executeQuery()}),
     * each hash map key corresponding to a column name/label in that row (see {@link ResultSetMetaData#getColumnLabel(int)}),
     * and each hash map value corresponding to that column's value in that row (see {@link ResultSet#getObject(int)})
     * @throws SQLException May be thrown by {@link PreparedStatement#executeQuery()} if the statement execution is unsuccessful
     */
    public static ArrayList<HashMap<String, Object>> executeQuery(String sql, Object... parameters) throws SQLException {
        try (OraclePreparedStatement statement = prepareStatement(sql, parameters)) {
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            ArrayList<HashMap<String, Object>> results = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Object> result = new HashMap<>();
                for (int index = 1; index <= columnCount; index++) {
                    String key = metaData.getColumnLabel(index);
                    Object value = resultSet.getObject(index);
                    result.put(key, value);
                }
                results.add(result);
            }
            return results;
        }
    }

    /**
     * @param sql        SQL statement to prepare and execute; see {@link #prepareStatement(String, Object...)}
     * @param parameters Parameters to bind to the SQL statement (if any); see {@link #prepareStatement(String, Object...)}
     * @return The number of rows updated by the statement execution (see {@link OraclePreparedStatement#executeUpdate()})
     * @throws SQLException May be thrown by {@link PreparedStatement#executeUpdate()} if the statement execution is unsuccessful
     */
    public static int executeUpdate(String sql, Object... parameters) throws SQLException {
        try (OraclePreparedStatement statement = prepareStatement(sql, parameters)) {
            return statement.executeUpdate();
        }
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
            displayErrorDialog("Encountered an error while getting database properties!\n\n%s", getStackTrace(exception));
        }
        return null;
    }
}
