package MealPlanner;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.driver.OracleDriver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import static MealPlanner.Main.outputException;

public class DatabaseHelper {
    private final static int LOGIN_TIMEOUT = 3;

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

            DriverManager.setLoginTimeout(LOGIN_TIMEOUT);
            connection = DriverManager.getConnection(url, username, password);

            return true;
        } catch (SQLTimeoutException exception) {
            outputException("Connection timed out after %d seconds!".formatted(LOGIN_TIMEOUT));
        } catch (SQLException exception) {
            outputException(exception);
        }
        return false;
    }

    /**
     * Closes the JDBC connection to the database; see {@link Connection#close()}
     */
    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException exception) {
            outputException(exception);
        }
        connection = null;
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
     * @return If the query was not successful, {@code null}; otherwise, an array of hash maps,
     * with each hash map corresponding to a row in the result set (see {@link OraclePreparedStatement#executeQuery()}),
     * each hash map key corresponding to a column name/label in that row (see {@link ResultSetMetaData#getColumnLabel(int)}),
     * and each hash map value corresponding to that column's value in that row (see {@link ResultSet#getObject(int)})
     */
    public static ArrayList<HashMap<String, Object>> executeQuery(String sql, Object... parameters) {
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
        } catch (SQLException exception) {
            outputException(exception);
        }
        return null;
    }

    /**
     * @param sql        SQL statement to prepare and execute; see {@link #prepareStatement(String, Object...)}
     * @param parameters Parameters to bind to the SQL statement (if any); see {@link #prepareStatement(String, Object...)}
     * @return If the query was not successful, {@code -1}; otherwise, the number of rows updated by the query (see {@link OraclePreparedStatement#executeUpdate()})
     */
    public static int executeUpdate(String sql, Object... parameters) {
        try (OraclePreparedStatement statement = prepareStatement(sql, parameters)) {
            return statement.executeUpdate();
        } catch (SQLException exception) {
            outputException(exception);
        }
        return -1;
    }

    /**
     * @return If an error occurred while reading the {@code database.properties} resource file, {@code null};
     * otherwise, a {@link Properties} object containing the properties in the {@code database.properties} resource file
     * (see {@link ClassLoader#getResourceAsStream(String)} and {@link Properties#load(InputStream)})
     */
    private static Properties getProperties() {
        try (InputStream stream = DatabaseHelper.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (stream == null) {
                outputException("Failed to obtain database properties! Make sure to follow the instructions in the README.");
                return null;
            }

            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException exception) {
            outputException(exception);
        }
        return null;
    }
}
