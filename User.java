package fitnesstracker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class User {
    private String name;
    private int age;
    private double weightKg;
    private double heightCm;
    private String sex;

    // Progress tracking: daily totals (date -> calories burned that day)
    private Map<LocalDate, Double> dailyTotals = new HashMap<>();

    // Goals
    private int dailyCalorieGoal = 0; // calories per day goal
    private double weightGoalKg = 0.0; // desired weight

    // Avatar path (optional)
    private String avatarPath = null;

        // Water intake tracking
        private int dailyWaterGoalMl = 0;
        private int waterTodayMl = 0;
        private List<WaterRecord> waterRecords = new ArrayList<>();
        private LocalDate lastWaterDate = LocalDate.now();

    public User(String name, int age, double weightKg, double heightCm, String sex) {
        this.name = name;
        this.age = age;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.sex = sex;
        this.dailyWaterGoalMl = (int)(weightKg * 35); // Default: weight * 35ml
        this.waterTodayMl = 0;
        this.waterRecords = new ArrayList<>();
        this.lastWaterDate = LocalDate.now();
    }

    // Exercise history for this user
    private java.util.List<ExerciseEntry> history = new java.util.ArrayList<>();

    public java.util.List<ExerciseEntry> getHistory() {
        return java.util.Collections.unmodifiableList(history);
    }

    public void clearHistory() {
        history.clear();
        dailyTotals.clear();
    }

    /**
     * Aggregate calories per exercise name (sum of calories for same exercise name)
     */
    public java.util.Map<String, Double> aggregateCaloriesPerExercise() {
        java.util.Map<String, Double> map = new java.util.HashMap<>();
        for (ExerciseEntry e : history) {
            map.put(e.getExerciseName(), map.getOrDefault(e.getExerciseName(), 0.0) + e.getCalories());
        }
        return map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public double getHeightMeters() {
        return heightCm / 100.0;
    }

    public double calculateBMI() {
        double h = getHeightMeters();
        if (h <= 0) return 0;
        return weightKg / (h * h);
    }

    @Override
    public String toString() {
        return name + " (" + age + ")";
    }

    // -------- Progress & Goals API --------
    public void setDailyCalorieGoal(int kcal) { this.dailyCalorieGoal = kcal; }
    public int getDailyCalorieGoal() { return this.dailyCalorieGoal; }

    public void setWeightGoalKg(double kg) { this.weightGoalKg = kg; }
    public double getWeightGoalKg() { return this.weightGoalKg; }

    public void setAvatarPath(String path) { this.avatarPath = path; }
    public String getAvatarPath() { return this.avatarPath; }

    // -------- Water Intake API --------
    public int getDailyWaterGoalMl() { return dailyWaterGoalMl; }
    public void setDailyWaterGoalMl(int goal) { this.dailyWaterGoalMl = goal; }
    public int getWaterTodayMl() { resetWaterIfNewDay(); return waterTodayMl; }
    public void addWater(int amount) {
        resetWaterIfNewDay();
        waterTodayMl += amount;
        waterRecords.add(new WaterRecord(amount, java.time.LocalDateTime.now()));
    }
    public void resetWaterIfNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastWaterDate)) {
            waterTodayMl = 0;
            lastWaterDate = today;
        }
    }
    public List<WaterRecord> getWaterRecords() { return waterRecords; }
    public void setWaterRecords(List<WaterRecord> records) {
        this.waterRecords = records;
        // Recompute today's total
        waterTodayMl = 0;
        lastWaterDate = LocalDate.now();
        for (WaterRecord r : records) {
            if (r.getTimestamp().toLocalDate().equals(lastWaterDate)) {
                waterTodayMl += r.getAmount();
            }
        }
    }

    // When adding an ExerciseEntry, also update daily totals
    public void addExerciseEntry(ExerciseEntry e) {
        if (e == null) return;
        if (history == null) history = new ArrayList<>();
        history.add(e);
        LocalDate d = Instant.ofEpochMilli(e.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate();
        dailyTotals.put(d, dailyTotals.getOrDefault(d, 0.0) + e.getCalories());
    }

    // Recompute daily totals from history (useful after clearing or bulk load)
    public void recomputeDailyTotals() {
        dailyTotals.clear();
        if (history == null) return;
        for (ExerciseEntry e : history) {
            LocalDate d = Instant.ofEpochMilli(e.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate();
            dailyTotals.put(d, dailyTotals.getOrDefault(d, 0.0) + e.getCalories());
        }
    }

    // Get calories for a specific day
    public double getCaloriesForDate(LocalDate date) {
        return dailyTotals.getOrDefault(date, 0.0);
    }

    // Get calories total for the 7 days ending at 'end' (inclusive)
    public double getCaloriesFor7Days(LocalDate end) {
        double sum = 0.0;
        for (int i = 0; i < 7; i++) {
            LocalDate d = end.minusDays(i);
            sum += getCaloriesForDate(d);
        }
        return sum;
    }

    // Return a copy of daily totals map
    public Map<LocalDate, Double> getDailyTotalsMap() {
        return new HashMap<>(dailyTotals);
    }
}
