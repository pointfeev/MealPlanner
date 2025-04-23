#!/usr/bin/env bash
./gradlew installDist -q || exit
./build/install/MealPlanner/bin/MealPlanner "$@"