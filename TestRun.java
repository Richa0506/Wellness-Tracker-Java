package fitnesstracker;

import java.io.File;
import java.util.List;

public class TestRun {
    public static void main(String[] args) {
        User u = new User("Alice", 30, 65.0, 170.0, "Female");
        Exercise e = new Exercise("Jogging (6 mph)", 10.0);

        System.out.println("=== Console Test Run ===");
        System.out.println("User: " + u.getName());
        System.out.printf("BMI: %.2f\n", u.calculateBMI());

        double kcal = e.getMet() * 3.5 * u.getWeightKg() / 200.0 * 60.0;
        System.out.printf("Calories for %s (1h): %.1f kcal\n", e.getName(), kcal);

        try {
            File f = new File(System.getProperty("user.home"), "fitness_users_test.csv");
            FitnessDataManager.saveUsers(List.of(u), f);
            System.out.println("Saved test users to: " + f.getAbsolutePath());

            List<User> loaded = FitnessDataManager.loadUsers(f);
            System.out.println("Loaded users from file:");
            for (User lu : loaded) {
                System.out.printf(" - %s, age=%d, weight=%.2fkg, height=%.2fcm\n",
                        lu.getName(), lu.getAge(), lu.getWeightKg(), lu.getHeightCm());
            }
        } catch (Exception ex) {
            System.out.println("Error during file save/load:");
            ex.printStackTrace();
        }

        System.out.println("=== End Test ===");
    }
}
