# Fitness Tracker (Java Swing)

This is a simple object-oriented fitness-tracking application in Java using Swing.

Features
- Multiple user support (add/update/remove users)
- Calculate BMI for a selected user
- Estimate calories burned after 1 hour for a selected exercise (uses MET values)
- Save/load user fitness data to a CSV file (`fitness_users.csv` in the user home directory)

Files
- `src/fitnesstracker/User.java` — user model with BMI calculation
- `src/fitnesstracker/Exercise.java` — exercise model with MET
- `src/fitnesstracker/FitnessDataManager.java` — simple CSV save/load
- `src/fitnesstracker/GUI.java` — Swing user interface
- `src/fitnesstracker/Main.java` — application entry point

Build & Run (macOS / Linux / Windows with JDK installed)

Compile:
```bash
javac -d out src/fitnesstracker/*.java
```

Run:
```bash
java -cp out fitnesstracker.Main
```

The application will create `fitness_users.csv` in your home directory when you save users.

Notes
- Calories per hour use the formula: kcal/min = (MET * 3.5 * weightKg) / 200; multiplied by 60 for an hour.
- This is a small demo app intended to be extended — you can add user history, JSON storage, or a database.
