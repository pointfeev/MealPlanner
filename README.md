# Meal Planner
CSC 545 Term Project

A database application developed using Java, Swing and Oracle JDBC that allows for managing a collection of recipes, keeping track of current items in the fridge/pantry, managing a weekly meal plan, and automatically generating a shopping list according to the weekly meal plans.

To allow the program to connect to a database, copy the `database.properties.example` file to the `src/main/resources`
folder, rename it to `database.properties`, and edit it to contain your Oracle database URL, username and password.

To install and run on **Windows**, use `.\Run.bat`.

To install and run on **Linux**, use `./Run.sh`.

If you encounter any issues running the application, make sure you have installed the latest Temurin JDK LTS (see https://adoptium.net/installation); this application was tested with Temurin JDK version 21.
