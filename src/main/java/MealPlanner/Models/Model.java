package MealPlanner.Models;

import MealPlanner.DatabaseHelper;
import MealPlanner.Models.Annotations.Ignore;
import MealPlanner.Models.Annotations.PrimaryKey;
import oracle.jdbc.OraclePreparedStatement;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static MealPlanner.GUI.MainFrame.displayErrorDialog;

public class Model {
    private Class<? extends Model> model;
    private String modelName;
    private String table;

    /**
     * @return Whether reflection data was successfully populated
     */
    private boolean populateReflectionData() {
        try {
            if (model == null) {
                model = getClass();
            }
            if (modelName == null) {
                modelName = model.getSimpleName();
            }
            if (table == null) {
                table = model.getDeclaredField("TABLE").get(this).toString();
            }
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering reflection data for %s!\n\n%s", modelName, exception);
            return false;
        }
        return true;
    }

    /**
     * Inserts the current object into the associated database table
     *
     * @return {@code true} if the insertion was successful, otherwise {@code false}
     */
    public boolean insert() {
        if (!populateReflectionData()) {
            return false;
        }

        ArrayList<String> returnColumns = new ArrayList<>();

        StringBuilder parametersBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        ArrayList<Object> parameterValues = new ArrayList<>();

        try {
            for (Field field : model.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String fieldName = field.getName();
                Object value = field.get(this);
                if (field.getAnnotation(PrimaryKey.class) != null && value == null) {
                    returnColumns.add(fieldName);
                    continue;
                }

                if (!parametersBuilder.isEmpty()) {
                    parametersBuilder.append(", ");
                }
                parametersBuilder.append(fieldName);

                if (!valuesBuilder.isEmpty()) {
                    valuesBuilder.append(", ");
                }
                valuesBuilder.append("?");
                parameterValues.add(value);
            }
        } catch (IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering insertion parameters for %s!\n\n%s", modelName, exception);
            return false;
        }

        if (parametersBuilder.isEmpty()) {
            displayErrorDialog("Missing parameter fields for class %s!", modelName);
            return false;
        }

        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(
                "INSERT INTO %s (%s) VALUES (%s)".formatted(table, parametersBuilder.toString(), valuesBuilder.toString()),
                returnColumns.toArray(new String[0]), parameterValues.toArray())) {
            if (statement.executeUpdate() == 0) {
                displayErrorDialog("Failed to perform an insertion for %s!", modelName);
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    return true;
                }

                try {
                    for (int index = 0; index < returnColumns.size(); index++) {
                        String column = returnColumns.get(index);
                        model.getField(column).set(this, generatedKeys.getObject(index + 1));
                    }
                } catch (NoSuchFieldException | IllegalAccessException exception) {
                    displayErrorDialog("Encountered an error while parsing insertion generated keys for %s!\n\n%s", modelName, exception);
                    return false;
                }
            }
        } catch (SQLException exception) {
            displayErrorDialog("Encountered an error while performing an insertion for %s!\n\n%s", modelName, exception);
            return false;
        }
        return true;
    }

    /**
     * Updates the current object in the associated database table
     *
     * @return {@code true} if the update was successful, otherwise {@code false}
     */
    public boolean update() {
        if (!populateReflectionData()) {
            return false;
        }

        StringBuilder keysBuilder = new StringBuilder();
        ArrayList<Object> keyKeysAndValuesList = new ArrayList<>();

        StringBuilder parametersBuilder = new StringBuilder();
        ArrayList<Object> parameterKeysAndValuesList = new ArrayList<>();

        try {
            for (Field field : model.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String fieldName = field.getName();

                if (field.getAnnotation(PrimaryKey.class) != null) {
                    if (!keysBuilder.isEmpty()) {
                        keysBuilder.append(", ");
                    }
                    keysBuilder.append("%s = ?".formatted(fieldName));
                    keyKeysAndValuesList.add(field.get(this));
                    continue;
                }

                if (!parametersBuilder.isEmpty()) {
                    parametersBuilder.append(", ");
                }
                parametersBuilder.append("%s = ?".formatted(fieldName));
                parameterKeysAndValuesList.add(field.get(this));
            }
        } catch (IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering update parameters for %s!\n\n%s", modelName, exception);
        }

        if (parametersBuilder.isEmpty()) {
            displayErrorDialog("Missing parameter fields for class %s!", modelName);
            return false;
        }
        if (keysBuilder.isEmpty()) {
            displayErrorDialog("Missing primary key field(s) for class %s!", modelName);
            return false;
        }

        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(
                "UPDATE %s SET %s WHERE %s".formatted(table, parametersBuilder.toString(), keysBuilder.toString()),
                parameterKeysAndValuesList.toArray(), keyKeysAndValuesList.toArray())) {
            if (statement.executeUpdate() == 0) {
                displayErrorDialog("Failed to perform an update for %s!", modelName);
                return false;
            }
        } catch (SQLException exception) {
            displayErrorDialog("Encountered an error while performing an update for %s!\n\n%s", modelName, exception);
            return false;
        }
        return true;
    }

    /**
     * Deletes the current object from the associated database table
     *
     * @return {@code true} if the deletion was successful, otherwise {@code false}
     */
    public boolean delete() {
        if (!populateReflectionData()) {
            return false;
        }

        StringBuilder keysBuilder = new StringBuilder();
        ArrayList<Object> keyKeysAndValuesList = new ArrayList<>();

        try {
            for (Field field : model.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                if (field.getAnnotation(PrimaryKey.class) == null) {
                    continue;
                }

                String fieldName = field.getName();

                if (!keysBuilder.isEmpty()) {
                    keysBuilder.append(", ");
                }
                keysBuilder.append("%s = ?".formatted(fieldName));
                keyKeysAndValuesList.add(field.get(this));
            }
        } catch (IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering deletion parameters for %s!\n\n%s", modelName, exception);
        }

        if (keysBuilder.isEmpty()) {
            displayErrorDialog("Missing primary key field(s) for class %s!", modelName);
            return false;
        }

        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(
                "DELETE FROM %s WHERE %s".formatted(table, keysBuilder.toString()),
                keyKeysAndValuesList.toArray())) {
            if (statement.executeUpdate() == 0) {
                displayErrorDialog("Failed to perform a deletion for %s!", modelName);
                return false;
            }
        } catch (SQLException exception) {
            displayErrorDialog("Encountered an error while performing a deletion for %s!\n\n%s", modelName, exception);
            return false;
        }
        return true;
    }
}
