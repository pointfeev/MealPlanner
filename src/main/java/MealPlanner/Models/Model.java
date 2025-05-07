package MealPlanner.Models;

import MealPlanner.DatabaseHelper;
import MealPlanner.Models.Annotations.*;
import oracle.jdbc.OraclePreparedStatement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static MealPlanner.Main.displayErrorDialog;

public abstract class Model {
    private Class<? extends Model> modelClass;
    private String modelName;
    private String table;

    /**
     * Populates reflection data required for the current instance, including the model class,
     * model name, and associated database table name. Reflection is used to retrieve metadata
     * about the model class and its fields dynamically at runtime.
     * <p>
     * In the case of reflection-related exceptions (e.g., if the `TABLE` field does not exist or
     * is inaccessible), an error message will be displayed, and the method will return false.
     *
     * @return {@code true} if the reflection data was successfully gathered; {@code false} if
     * an error occurred during the process.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean populateReflectionData() {
        try {
            if (modelClass == null) {
                modelClass = getClass();
            }
            if (modelName == null) {
                modelName = modelClass.getSimpleName();
            }
            if (table == null) {
                table = modelClass.getDeclaredField("TABLE").get(this).toString();
            }
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering reflection data for %s!\n\n%s", modelName, exception);
            return false;
        }
        return true;
    }

    /**
     * Executes a database SELECT operation for the current model instance by dynamically
     * constructing a query based on the fields of the associated model class. Fields marked
     * with {@link Ignore} are excluded. Optionally, fields annotated with {@link OrderBy} are
     * used for ordering the results.
     * <p>
     * Reflection is used to populate the query's WHERE and ORDER BY clauses dynamically based
     * on the fields' values and annotations. Results are mapped back to model objects of the
     * associated type.
     * <p>
     * In case of reflection-related errors, SQL exceptions, or instantiation failures,
     * appropriate error dialogs are displayed, and the method will return {@code null}.
     *
     * @param <T> the type of the model extending {@link Model}
     * @return an array of objects of type {@code T} representing the query result, or {@code null}
     * if an error occurs during the process.
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> T[] select() {
        if (!populateReflectionData()) {
            return null;
        }

        StringBuilder whereBuilder = new StringBuilder();
        ArrayList<Object> whereValues = new ArrayList<>();

        StringBuilder orderByBuilder = new StringBuilder();

        try {
            for (Field field : modelClass.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                Object value = field.get(this);
                if (value != null) {
                    if (!whereBuilder.isEmpty()) {
                        whereBuilder.append(" AND ");
                    }
                    if (value instanceof String && (((String) value).contains("%") || ((String) value).contains("_"))) {
                        whereBuilder.append("%s LIKE ?".formatted(field.getName()));
                    } else {
                        whereBuilder.append("%s = ?".formatted(field.getName()));
                    }
                    whereValues.add(value);
                }

                OrderBy orderBy = field.getAnnotation(OrderBy.class);
                if (orderBy != null) {
                    if (!orderByBuilder.isEmpty()) {
                        orderByBuilder.append(", ");
                    }
                    orderByBuilder.append(field.getName());

                    String order = orderBy.value();
                    if (order != null) {
                        orderByBuilder.append(" ").append(order);
                    }
                }
            }
        } catch (IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering selection parameters for %s!\n\n%s", modelName, exception);
            return null;
        }

        ArrayList<T> results = new ArrayList<>();
        String sql = "SELECT * FROM %s%s%s".formatted(table,
                whereBuilder.isEmpty() ? "" : " WHERE %s".formatted(whereBuilder),
                orderByBuilder.isEmpty() ? "" : " ORDER BY %s".formatted(orderByBuilder));
        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(sql, whereValues.toArray())) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    try {
                        T result = (T) modelClass.getDeclaredConstructor().newInstance();
                        for (Field field : modelClass.getFields()) {
                            if (field.getAnnotation(Ignore.class) != null) {
                                continue;
                            }

                            Object value;
                            if (field.getType() == Date.class) {
                                // JDBC tries to return Timestamp for the Date class, causing errors
                                value = resultSet.getDate(field.getName());
                            } else {
                                value = resultSet.getObject(field.getName());
                            }
                            field.set(result, value);
                        }
                        results.add(result);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                             InvocationTargetException exception) {
                        displayErrorDialog("Encountered an error while parsing selection results for %s!\n\n%s", modelName, exception);
                        return null;
                    }
                }
            }
        } catch (SQLException exception) {
            displayErrorDialog("Encountered an error while performing a selection for %s!\n\n%s", modelName, exception);
            return null;
        }

        T[] array = (T[]) java.lang.reflect.Array.newInstance(modelClass, results.size());
        return results.toArray(array);
    }

    /**
     * Validates the current instance of the model by checking its fields against
     * specified constraints, such as annotations {@code @NotNull}, {@code @CheckString},
     * and {@code @CheckNumber}.
     * <p>
     * The method dynamically evaluates fields using reflection and applies the
     * following validation criteria:
     * - Fields annotated with {@code @NotNull} must not be null or blank.
     * - Fields annotated with {@code @CheckString} must match one of the allowed values.
     * - Fields annotated with {@code @CheckNumber} must fall within the specified range.
     * - Fields annotated with {@code @Ignore} are skipped during validation.
     * <p>
     * If a validation failure occurs, an error dialog is displayed with the
     * corresponding message, and the method returns {@code false}. If an
     * {@code IllegalAccessException} occurs during reflection, an error dialog
     * is also displayed, but the method may still return {@code true} unless
     * validation criteria are not met.
     *
     * @return {@code true} if all validation criteria are met; {@code false} otherwise.
     */
    public boolean validate() {
        if (!populateReflectionData()) {
            displayErrorDialog("Failed to populate reflection data!");
            return false;
        }

        try {
            for (Field field : modelClass.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                Object value = field.get(this);
                if (value == null || (value instanceof String && ((String) value).isBlank())) {
                    if (field.getAnnotation(NotNull.class) != null) {
                        displayErrorDialog("Please enter a valid value for field '%s'!".formatted(field.getName()));
                        return false;
                    }
                    continue;
                }

                CheckString checkString = field.getAnnotation(CheckString.class);
                if (checkString != null) {
                    String valueString = (String) value;

                    String[] checkValues = checkString.value();
                    boolean found = false;
                    for (String checkValue : checkValues) {
                        if (valueString.equals(checkValue)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        displayErrorDialog("Value for field '%s' must be one of the following: %s".formatted(field.getName(), String.join(", ", checkValues)));
                        return false;
                    }
                }

                CheckNumber checkNumber = field.getAnnotation(CheckNumber.class);
                if (checkNumber != null) {
                    Number valueNumber = (Number) value;

                    int checkMin = checkNumber.min();
                    int checkMax = checkNumber.max();
                    if (valueNumber.doubleValue() < checkMin || valueNumber.doubleValue() > checkMax) {
                        displayErrorDialog("Value for field '%s' must be between %d and %d!".formatted(field.getName(), checkMin, checkMax));
                        return false;
                    }
                }
            }
        } catch (IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while performing validation for %s!\n\n%s".formatted(modelName, exception));
            return false;
        }
        return true;
    }

    /**
     * Inserts the current instance of the model into the associated database table by dynamically
     * constructing an SQL INSERT statement using reflection. Fields annotated with {@link Ignore}
     * are skipped during insertion. Fields annotated with {@link PrimaryKey} are included
     * conditionally, depending on whether their values are provided.
     * <p>
     * If the insertion is successful, any auto-generated keys are retrieved and populated
     * back into the respective fields of the model instance.
     * <p>
     * Reflection errors, SQL-related exceptions, or missing required fields will result in an
     * error message being displayed, and the method will return {@code false}.
     *
     * @return {@code true} if the model was successfully inserted into the database;
     * {@code false} otherwise.
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
            for (Field field : modelClass.getFields()) {
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

        String sql = "INSERT INTO %s (%s) VALUES (%s)".formatted(table, parametersBuilder.toString(), valuesBuilder.toString());
        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(sql, returnColumns.toArray(new String[0]), parameterValues.toArray())) {
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
                        modelClass.getField(column).set(this, generatedKeys.getObject(index + 1));
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
     * Updates the current instance of the model in the associated database table by dynamically constructing
     * an SQL UPDATE statement using reflection. Fields annotated with {@link Ignore} are skipped during the update.
     * Primary key fields, annotated with {@link PrimaryKey}, are used in the WHERE clause to identify the target row(s).
     * <p>
     * The method dynamically evaluates fields of the model class to populate the SET clause of the update query.
     * A failure in constructing the query, missing primary key(s), or database-related exceptions will result in
     * an error message being displayed, and the method returning {@code false}.
     *
     * @return {@code true} if the model was successfully updated in the database; {@code false} otherwise.
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
            for (Field field : modelClass.getFields()) {
                if (field.getAnnotation(Ignore.class) != null) {
                    continue;
                }

                String fieldName = field.getName();

                if (field.getAnnotation(PrimaryKey.class) != null) {
                    if (!keysBuilder.isEmpty()) {
                        keysBuilder.append(" AND ");
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
            return false;
        }

        if (parametersBuilder.isEmpty()) {
            displayErrorDialog("Missing parameter fields for class %s!", modelName);
            return false;
        }
        if (keysBuilder.isEmpty()) {
            displayErrorDialog("Missing primary key field(s) for class %s!", modelName);
            return false;
        }

        String sql = "UPDATE %s SET %s WHERE %s".formatted(table, parametersBuilder.toString(), keysBuilder.toString());
        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(sql, parameterKeysAndValuesList.toArray(), keyKeysAndValuesList.toArray())) {
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
     * Deletes the current model instance from the associated database table.
     * This method uses reflection to identify fields annotated with {@link PrimaryKey},
     * which are used to construct the WHERE clause of the SQL DELETE statement.
     * <p>
     * The deletion is performed only if the primary key field(s) are properly defined
     * and the required data can be gathered through reflection. Any database-related
     * errors or reflection-related issues will result in an error message being displayed,
     * and the deletion will not be executed.
     *
     * @return {@code true} if the deletion was successful; {@code false} otherwise.
     */
    public boolean delete() {
        if (!populateReflectionData()) {
            return false;
        }

        StringBuilder keysBuilder = new StringBuilder();
        ArrayList<Object> keyKeysAndValuesList = new ArrayList<>();

        try {
            for (Field field : modelClass.getFields()) {
                if (field.getAnnotation(PrimaryKey.class) == null) {
                    continue;
                }

                String fieldName = field.getName();

                if (!keysBuilder.isEmpty()) {
                    keysBuilder.append(" AND ");
                }
                keysBuilder.append("%s = ?".formatted(fieldName));
                keyKeysAndValuesList.add(field.get(this));
            }
        } catch (IllegalAccessException exception) {
            displayErrorDialog("Encountered an error while gathering deletion parameters for %s!\n\n%s", modelName, exception);
            return false;
        }

        if (keysBuilder.isEmpty()) {
            displayErrorDialog("Missing primary key field(s) for class %s!", modelName);
            return false;
        }

        String sql = "DELETE FROM %s WHERE %s".formatted(table, keysBuilder.toString());
        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(sql, keyKeysAndValuesList.toArray())) {
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
