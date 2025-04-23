@echo off
call gradlew.bat installDist -q || exit
call build/install/MealPlanner/bin/MealPlanner.bat %*