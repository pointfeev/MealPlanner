package MealPlanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Connecting to the database...");
        if (!DatabaseHelper.connect()) {
            return;
        }
        System.out.println("Connected successfully, running a query...");
        try {
            ArrayList<HashMap<String, Object>> results = DatabaseHelper.executeQuery("SELECT table_name FROM all_tables WHERE owner LIKE '%545'");
            if (results == null) {
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
}