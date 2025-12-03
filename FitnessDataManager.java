package fitnesstracker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FitnessDataManager {
    // Save users to CSV: name,age,weightKg,heightCm,sex,history
    // history is serialized as entries separated by ';' where each entry is name|calories|timestamp
    public static void saveUsers(List<User> users, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // header: name,age,weightKg,heightCm,sex,history,dailyCalorieGoal,weightGoalKg,avatarPath,dailyWaterGoalMl,waterRecords
            pw.println("name,age,weightKg,heightCm,sex,history,dailyCalorieGoal,weightGoalKg,avatarPath,dailyWaterGoalMl,waterRecords");
            for (User u : users) {
                String hist = serializeHistory(u);
                StringBuilder waterRecordsStr = new StringBuilder();
                for (WaterRecord wr : u.getWaterRecords()) {
                    waterRecordsStr.append(wr.getTimestamp().toLocalDate()).append("|").append(wr.getAmount()).append(";");
                }
                pw.printf("%s,%d,%.2f,%.2f,%s,%s,%d,%.2f,%s,%d,%s%n",
                        escapeField(u.getName()), u.getAge(), u.getWeightKg(), u.getHeightCm(),
                        escapeField(u.getSex()), escapeField(hist), u.getDailyCalorieGoal(), u.getWeightGoalKg(), escapeField(u.getAvatarPath()),
                        u.getDailyWaterGoalMl(), waterRecordsStr.toString());
            }
        }
    }

    // Load users from CSV
    public static List<User> loadUsers(File file) throws IOException {
        List<User> users = new ArrayList<>();
        if (!file.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = splitCsv(line);
                if (parts.length >= 5) {
                    String name = unescape(parts[0]);
                    int age = parseIntSafe(parts[1]);
                    double weight = parseDoubleSafe(parts[2]);
                    double height = parseDoubleSafe(parts[3]);
                    String sex = unescape(parts[4]);
                    User user = new User(name, age, weight, height, sex);
                    // history may be in parts[5]
                    if (parts.length >= 6) {
                        String histRaw = unescapeField(parts[5]);
                        try { parseHistoryIntoUser(histRaw, user); } catch (Exception ex) { /* ignore malformed history */ }
                        // recompute derived daily totals from loaded history
                        try { user.recomputeDailyTotals(); } catch (Exception e) { /* ignore */ }
                    }
                    // optional fields: dailyCalorieGoal (6), weightGoalKg (7), avatarPath (8), dailyWaterGoalMl (9), waterRecords (10)
                    if (parts.length >= 7) {
                        int goal = parseIntSafe(unescapeField(parts[6]));
                        user.setDailyCalorieGoal(goal);
                    }
                    if (parts.length >= 8) {
                        double wgoal = parseDoubleSafe(unescapeField(parts[7]));
                        user.setWeightGoalKg(wgoal);
                    }
                    if (parts.length >= 9) {
                        String avatar = unescapeField(parts[8]);
                        user.setAvatarPath(avatar);
                    }
                    if (parts.length >= 10) {
                        int waterGoal = parseIntSafe(unescapeField(parts[9]));
                        user.setDailyWaterGoalMl(waterGoal);
                    }
                    if (parts.length >= 11) {
                        List<WaterRecord> waterRecords = new ArrayList<>();
                        String[] records = parts[10].split(";");
                        for (String rec : records) {
                            if (rec.trim().isEmpty()) continue;
                            String[] wparts = rec.split("\\|");
                            if (wparts.length == 2) {
                                try {
                                    java.time.LocalDate date = java.time.LocalDate.parse(wparts[0]);
                                    int amount = Integer.parseInt(wparts[1]);
                                    waterRecords.add(new WaterRecord(amount, date.atStartOfDay()));
                                } catch (Exception e) { /* skip invalid */ }
                            }
                        }
                        user.setWaterRecords(waterRecords);
                    }
                    users.add(user);
                }
            }
        }
        return users;
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace(",", "\\,");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\,", ",");
    }

    // Escape a whole field (also escapes backslash and separators used in history)
    private static String escapeField(String s) {
        if (s == null) return "";
        // escape backslash first
        String out = s.replace("\\", "\\\\");
        out = out.replace("|", "\\|");
        out = out.replace(";", "\\;");
        out = out.replace(",", "\\,");
        out = out.replace("\n", " ");
        return out;
    }

    private static String unescapeField(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (esc) { sb.append(c); esc = false; }
            else if (c == '\\') esc = true;
            else sb.append(c);
        }
        return sb.toString();
    }

    // Serialize a user's history into a compact string
    private static String serializeHistory(User u) {
        StringBuilder sb = new StringBuilder();
        for (ExerciseEntry e : u.getHistory()) {
            if (sb.length() > 0) sb.append(';');
            String name = e.getExerciseName();
            String calories = String.format("%.2f", e.getCalories());
            String ts = Long.toString(e.getTimestamp());
            String entry = escapeField(name) + "|" + escapeField(calories) + "|" + escapeField(ts);
            sb.append(entry);
        }
        return sb.toString();
    }

    private static void parseHistoryIntoUser(String histRaw, User user) {
        if (histRaw == null || histRaw.isEmpty()) return;
        StringBuilder cur = new StringBuilder();
        boolean esc = false;
        java.util.List<String> entries = new java.util.ArrayList<>();
        for (int i = 0; i < histRaw.length(); i++) {
            char c = histRaw.charAt(i);
            if (esc) { cur.append(c); esc = false; }
            else if (c == '\\') esc = true;
            else if (c == ';') { entries.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        if (cur.length() > 0) entries.add(cur.toString());
        for (String ent : entries) {
            // split by unescaped '|'
            StringBuilder part = new StringBuilder();
            java.util.List<String> parts = new java.util.ArrayList<>();
            esc = false;
            for (int i = 0; i < ent.length(); i++) {
                char c = ent.charAt(i);
                if (esc) { part.append(c); esc = false; }
                else if (c == '\\') esc = true;
                else if (c == '|') { parts.add(part.toString()); part.setLength(0); }
                else part.append(c);
            }
            if (part.length() > 0) parts.add(part.toString());
            if (parts.size() >= 3) {
                String name = unescapeField(parts.get(0));
                double cal = parseDoubleSafe(unescapeField(parts.get(1)));
                long ts = 0;
                try { ts = Long.parseLong(unescapeField(parts.get(2))); } catch (Exception ex) { ts = System.currentTimeMillis(); }
                user.addExerciseEntry(new ExerciseEntry(name, cal, ts));
            }
        }
    }

    private static String[] splitCsv(String line) {
        // simple split that handles escaped commas \,
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean escape = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escape) {
                cur.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == ',') {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
