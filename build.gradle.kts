plugins {
    application
}

application {
    mainClass = "MealPlanner.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.oracle.database.jdbc:ojdbc17:23.7.0.25.01")
}