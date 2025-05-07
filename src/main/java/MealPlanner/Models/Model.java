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
     * Selects and retrieves an array of objects from the associated database table based on the field values of the current instance.
     * <p>
     * The method constructs a "WHERE" clause dynamically by evaluating the non-null fields of the current instance.
     * If no fields are non-null, all records in the table are returned.
     * <p>
     * The retrieved records are mapped to instances of the associated model class, with fields populated from the database.
     *
     * @param <T> The type of the model extending {@code Model}.
     * @return An array of objects of type {@code T}, representing the retrieved records, or {@code null} if an error occurs.
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
        try (OraclePreparedStatement statement = DatabaseHelper.prepareStatement(
                "SELECT * FROM %s%s%s".formatted(table,
                        whereBuilder.isEmpty() ? "" : " WHERE %s".formatted(whereBuilder),
                        orderByBuilder.isEmpty() ? "" : " ORDER BY %s".formatted(orderByBuilder)),
                whereValues.toArray())) {
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
     * Validates the current instance by checking field values against the associated constraints
     * provided through annotations such as {@link NotNull}, {@link CheckString}, and {@link CheckNumber}.
     * <p>
     * The validation process includes:
     * <p>
     * - Ensuring required fields (annotated with {@link NotNull}) are not null.
     * <p>
     * - Checking string values against allowed values (annotated with {@link CheckString}).
     * <p>
     * - Validating numeric values fall within a specified range (annotated with {@link CheckNumber}).
     * <p>
     * - Skipping fields annotated with {@link Ignore}.
     * <p>
     * If any validation fails, the method returns {@code false}.
     * If an {@link IllegalAccessException} arises during reflection, an error dialog is displayed.
     *
     * @return {@code true} if all validations pass; otherwise, {@code false}.
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
        }
        return true;
    }

    /**
     * Inserts the current object into the associated database table
     * <p>
     * The method dynamically constructs an SQL INSERT statement by examining the fields
     * of the current instance. Fields annotated with {@link Ignore} are excluded from
     * the insertion, and fields annotated with {@link PrimaryKey} are auto-generated if
     * their value is null. Reflection is used to gather field data, and generated keys
     * returned from the database are assigned back to the object.
     * <p>
     * If any step in the process fails, an error message is displayed, and the method returns {@code false}
     *
     * @return {@code true} if the object was successfully inserted into the database, otherwise {@code false}
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
     * Updates the current object in the associated database table
     * <p>
     * The method dynamically constructs an SQL UPDATE statement using reflection to identify
     * the fields and their values. Fields marked with the {@link Ignore} annotation are skipped,
     * while fields annotated with {@link PrimaryKey} are used in the WHERE clause. Other fields
     * are used in the SET clause.
     * <p>
     * If any step in the process fails, an error message will be displayed, and the method returns {@code false}
     *
     * @return {@code true} if the object was successfully updated in the database, otherwise {@code false}
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
     * <p>
     * The method dynamically builds an SQL DELETE statement using reflection to identify
     * primary key fields of the current instance. Fields annotated with {@link PrimaryKey}
     * are included in the WHERE clause of the deletion query.
     * <p>
     * If any step in the process fails, an error message will be displayed, and the method returns {@code false}
     *
     * @return {@code true} if the object was successfully deleted from the database, otherwise {@code false}
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
