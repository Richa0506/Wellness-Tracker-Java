package fitnesstracker;

public class ExerciseEntry {
    private String exerciseName;
    private double calories;
    private long timestamp;

    public ExerciseEntry(String exerciseName, double calories, long timestamp) {
        this.exerciseName = exerciseName;
        this.calories = calories;
        this.timestamp = timestamp;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public double getCalories() {
        return calories;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return exerciseName + " - " + String.format("%.1f kcal", calories);
    }
}
