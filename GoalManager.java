package fitnesstracker;

import java.time.LocalDate;

/**
 * GoalManager handles calorie goal logic and recommendations for a user.
 */
public class GoalManager {
    // Get total calories burned today for a user
    public static int getCaloriesBurnedToday(User user) {
        if (user == null) return 0;
        LocalDate today = LocalDate.now();
        return (int)Math.round(user.getCaloriesForDate(today));
    }

    // Get remaining calories to reach daily goal
    public static int getRemainingToGoal(User user) {
        if (user == null) return 0;
        int burned = getCaloriesBurnedToday(user);
        int goal = (int)Math.round(user.getDailyCalorieGoal());
        return goal - burned;
    }

    // Calculate BMR using Mifflin–St Jeor equation
    public static int getBMR(User user) {
        if (user == null) return 0;
        double w = user.getWeightKg();
        double h = user.getHeightCm();
        int age = user.getAge();
        String sex = user.getSex();
        double bmr;
        if (sex != null && sex.toLowerCase().startsWith("f")) {
            bmr = 10 * w + 6.25 * h - 5 * age - 161;
        } else {
            bmr = 10 * w + 6.25 * h - 5 * age + 5;
        }
        return (int)Math.round(bmr);
    }

    // Maintenance calories (light activity)
    public static int getMaintenanceCalories(User user) {
        return (int)Math.round(getBMR(user) * 1.4);
    }

    // Recommended intake for deficit
    public static int getRecommendedIntake(User user, int deficit) {
        return getMaintenanceCalories(user) - deficit;
    }
}