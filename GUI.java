package fitnesstracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.text.SimpleDateFormat;

public class GUI extends JFrame {
        // Water Intake Tracker UI fields
        private JTextField waterGoalField = new JTextField(6);
        private JLabel waterTodayLabel = new JLabel();
        private JLabel waterRemainingLabel = new JLabel();
        private JProgressBar waterProgressBar = new JProgressBar();
        private JTextField customWaterField = new JTextField(5);
        private Timer waterReminderTimer;

        // Water Intake Tracker logic
        private void updateWaterPanel() {
            User u = userJList.getSelectedValue();
            if (u == null) {
                waterGoalField.setText("");
                waterTodayLabel.setText("-");
                waterRemainingLabel.setText("-");
                waterProgressBar.setValue(0);
                waterProgressBar.setMaximum(100);
                waterProgressBar.setString("");
                return;
            }
            waterGoalField.setText(String.valueOf(u.getDailyWaterGoalMl()));
            int today = u.getWaterTodayMl();
            int goal = u.getDailyWaterGoalMl();
            waterTodayLabel.setText(today + " ml");
            int remaining = Math.max(goal - today, 0);
            if (goal > 0) {
                if (remaining <= 0) {
                    waterRemainingLabel.setText("\uD83C\uDF89 Goal reached! Great job staying hydrated!");
                    waterRemainingLabel.setForeground(new Color(0x2E8B57));
                } else {
                    waterRemainingLabel.setText(remaining + " ml");
                    waterRemainingLabel.setForeground(new Color(0x1F3D3D));
                }
                waterProgressBar.setMaximum(goal);
                waterProgressBar.setValue(Math.min(today, goal));
                waterProgressBar.setString(today + " / " + goal + " ml");
            } else {
                waterRemainingLabel.setText("-");
                waterRemainingLabel.setForeground(new Color(0x1F3D3D));
                waterProgressBar.setValue(0);
                waterProgressBar.setMaximum(100);
                waterProgressBar.setString("");
            }
        }

        private void onSetWaterGoal() {
            User u = userJList.getSelectedValue();
            if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
            String txt = waterGoalField.getText().trim();
            try {
                int val = Integer.parseInt(txt);
                if (val < 0) throw new NumberFormatException();
                u.setDailyWaterGoalMl(val);
                updateWaterPanel();
                JOptionPane.showMessageDialog(this, "Water goal updated for " + u.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number for water goal.");
            }
        }

        private void onAddWater(int amount) {
            User u = userJList.getSelectedValue();
            if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
            u.addWater(amount);
            updateWaterPanel();
        }

        private void onAddCustomWater() {
            User u = userJList.getSelectedValue();
            if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
            String txt = customWaterField.getText().trim();
            try {
                int val = Integer.parseInt(txt);
                if (val <= 0) throw new NumberFormatException();
                u.addWater(val);
                customWaterField.setText("");
                updateWaterPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number for water amount.");
            }
        }

        private void showWaterReminder() {
            JOptionPane.showMessageDialog(this, "\uD83D\uDCA7 Time to drink water! Stay hydrated!", "Water Reminder", JOptionPane.INFORMATION_MESSAGE);
        }
    private DefaultListModel<User> userListModel = new DefaultListModel<>();
    private JList<User> userJList = new JList<>(userListModel);

    private JTextField nameField = new JTextField(12);
    private JTextField ageField = new JTextField(3);
    private JTextField weightField = new JTextField(6);
    private JTextField heightField = new JTextField(6);
    private JComboBox<String> sexBox = new JComboBox<>(new String[]{"Male","Female","Other"});

    private JComboBox<Exercise> exerciseBox;
    private JTextField durationField = new JTextField(4); // minutes input for custom duration

    private JLabel bmiLabel = new JLabel("BMI: -");
    private JLabel calLabel = new JLabel("Calories (1h): -");

    // Calorie Goal Tracker UI
    private JTextField goalField = new JTextField(6);
    private JLabel burnedTodayLabel = new JLabel();
    private JLabel remainingLabel = new JLabel();
    private JLabel maintenanceLabel = new JLabel();
    private JLabel mildLabel = new JLabel();
    private JLabel moderateLabel = new JLabel();

    // Exercise history table
    private DefaultTableModel historyTableModel;
    private JTable historyTable;

    private File dataFile = new File(System.getProperty("user.home"), "fitness_users.csv");

    public GUI() {
        super("Fitness Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        // Root padding
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // Soft background
        getContentPane().setBackground(new Color(0xF3FBF8));

        List<Exercise> exercises = defaultExercises();
        exerciseBox = new JComboBox<>(exercises.toArray(new Exercise[0]));
        exerciseBox.setBackground(Color.WHITE);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(260);
        split.setBorder(null);

        // Left panel - users
        // Left panel - users (styled)
        RoundedPanel left = new RoundedPanel(new BorderLayout(), 12, new Color(0xFFFFFF));
        left.setBackground(new Color(0xF7FFFB));
        left.setBorder(new EmptyBorder(12,12,12,12));
        userJList.setBackground(new Color(0xFFFFFF));
        userJList.setBorder(new EmptyBorder(8,8,8,8));
        JScrollPane userScroll = new JScrollPane(userJList);
        userScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Users",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14), new Color(45,70,70)));
        left.add(userScroll, BorderLayout.CENTER);
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftButtons.setBackground(new Color(0,0,0,0));
        JButton removeBtn = styledButton("Remove User");
        removeBtn.addActionListener(e -> {
            User sel = userJList.getSelectedValue();
            if (sel != null) userListModel.removeElement(sel);
        });
        leftButtons.add(removeBtn);
        left.add(leftButtons, BorderLayout.SOUTH);

        // Right panel - details and actions
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(6, 12, 6, 12));

        // Calorie Goal Tracker panel
        JPanel goalPanel = new RoundedPanel(new GridBagLayout(), 14, new Color(0xAEE6CE));
        goalPanel.setBorder(BorderFactory.createTitledBorder(null, "Calorie Goal Tracker", TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 15), new Color(45,70,70)));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = 0; gc.gridy = 0; goalPanel.add(new JLabel("Enter Daily Goal (calories):"), gc);
        gc.gridx = 1; goalField.setMaximumSize(new Dimension(80,28)); goalPanel.add(goalField, gc);
        JButton setGoalBtn = styledButton("Set Goal");
        setGoalBtn.addActionListener(e -> onSetGoal());
        gc.gridx = 2; goalPanel.add(setGoalBtn, gc);
        gc.gridx = 0; gc.gridy = 1; goalPanel.add(new JLabel("Calories Burned Today:"), gc);
        gc.gridx = 1; gc.gridwidth = 2; goalPanel.add(burnedTodayLabel, gc);
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 1; goalPanel.add(new JLabel("Remaining Calories to Goal:"), gc);
        gc.gridx = 1; gc.gridwidth = 2; goalPanel.add(remainingLabel, gc);
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 1; goalPanel.add(new JLabel("Daily Maintenance Calories:"), gc);
        gc.gridx = 1; gc.gridwidth = 2; goalPanel.add(maintenanceLabel, gc);
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 1; goalPanel.add(new JLabel("Recommended Daily Intake (mild deficit):"), gc);
        gc.gridx = 1; gc.gridwidth = 2; goalPanel.add(mildLabel, gc);
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 1; goalPanel.add(new JLabel("Recommended Daily Intake (moderate deficit):"), gc);
        gc.gridx = 1; gc.gridwidth = 2; goalPanel.add(moderateLabel, gc);
        right.add(goalPanel);

            // Water Intake Tracker panel (separate section)
            JPanel waterPanel = new RoundedPanel(new GridBagLayout(), 14, new Color(0xB3E5FC));
            waterPanel.setBorder(BorderFactory.createTitledBorder(null, "\uD83D\uDCA7 Water Intake Tracker", TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 15), new Color(45,70,70)));
            GridBagConstraints wc = new GridBagConstraints();
            wc.insets = new Insets(8,8,8,8);
            wc.anchor = GridBagConstraints.WEST;
            wc.gridx = 0; wc.gridy = 0; waterPanel.add(new JLabel("Daily Water Goal (ml):"), wc);
            wc.gridx = 1; waterGoalField.setMaximumSize(new Dimension(80,28)); waterPanel.add(waterGoalField, wc);
            JButton setWaterGoalBtn = styledButton("Set Goal");
            setWaterGoalBtn.addActionListener(e -> onSetWaterGoal());
            wc.gridx = 2; waterPanel.add(setWaterGoalBtn, wc);
            wc.gridx = 0; wc.gridy = 1; wc.gridwidth = 1; waterPanel.add(new JLabel("\uD83D\uDCA7 Total Water Today:"), wc);
            wc.gridx = 1; wc.gridwidth = 2; waterPanel.add(waterTodayLabel, wc);
            wc.gridx = 0; wc.gridy = 2; wc.gridwidth = 1; waterPanel.add(new JLabel("Remaining to Goal:"), wc);
            wc.gridx = 1; wc.gridwidth = 2; waterPanel.add(waterRemainingLabel, wc);
            wc.gridx = 0; wc.gridy = 3; wc.gridwidth = 1; waterPanel.add(new JLabel("Add Water:"), wc);
            wc.gridx = 1; JButton add250Btn = styledButton("+250 ml \uD83C\uDF7C"); add250Btn.addActionListener(e -> onAddWater(250)); waterPanel.add(add250Btn, wc);
            wc.gridx = 2; JButton add500Btn = styledButton("+500 ml \uD83C\uDF7C"); add500Btn.addActionListener(e -> onAddWater(500)); waterPanel.add(add500Btn, wc);
            wc.gridx = 1; wc.gridy = 4; wc.gridwidth = 1; waterPanel.add(new JLabel("Custom (ml):"), wc);
            wc.gridx = 2; customWaterField.setMaximumSize(new Dimension(60,28)); waterPanel.add(customWaterField, wc);
            wc.gridx = 3; JButton addCustomBtn = styledButton("Add"); addCustomBtn.addActionListener(e -> onAddCustomWater()); waterPanel.add(addCustomBtn, wc);
            wc.gridx = 0; wc.gridy = 5; wc.gridwidth = 4; waterProgressBar.setStringPainted(true); waterPanel.add(waterProgressBar, wc);
            right.add(Box.createVerticalStrut(12));
            right.add(waterPanel);
            // Start water reminder timer (60 min)
            waterReminderTimer = new Timer(60 * 60 * 1000, e -> showWaterReminder());
            waterReminderTimer.setInitialDelay(60 * 60 * 1000);
            waterReminderTimer.start();

        JPanel form = new RoundedPanel(new GridBagLayout(), 10, new Color(0xFFFFFF));
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Name:"), c);
        c.gridx = 1; form.add(nameField, c);
        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Age:"), c);
        c.gridx = 1; form.add(ageField, c);
        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Weight (kg):"), c);
        c.gridx = 1; form.add(weightField, c);
        c.gridx = 0; c.gridy = 3; form.add(new JLabel("Height (cm):"), c);
        c.gridx = 1; form.add(heightField, c);
        c.gridx = 0; c.gridy = 4; form.add(new JLabel("Sex:"), c);
        c.gridx = 1; form.add(sexBox, c);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        JButton addBtn = styledButton("Add / Update User");
        addBtn.addActionListener(this::onAddUser);
        form.add(addBtn, c);

        right.add(form);

        JPanel calcPanel = new RoundedPanel(new FlowLayout(FlowLayout.LEFT), 10, new Color(0xFFFFFF));
        calcPanel.setOpaque(false);
        calcPanel.setBorder(new EmptyBorder(10,10,10,10));
        calcPanel.add(new JLabel("Select exercise:")); calcPanel.add(exerciseBox);
        JButton bmiBtn = styledButton("Calculate BMI");
        bmiBtn.addActionListener(e -> onCalculateBMI());
        calcPanel.add(bmiBtn);
        JButton calBtn = styledButton("Estimate Calories (1h)");
        calBtn.addActionListener(e -> onEstimateCalories());
        calcPanel.add(calBtn);
        // Duration-based calories (user input minutes) using fixed 8.5 kcal/min
        calcPanel.add(new JLabel("Duration (min):"));
        durationField.setMaximumSize(new Dimension(80, 28));
        calcPanel.add(durationField);
        JButton durBtn = styledButton("Estimate (by duration)");
        durBtn.addActionListener(e -> onEstimateByDuration());
        calcPanel.add(durBtn);
        right.add(calcPanel);

        JPanel outPanel = new RoundedPanel(new GridLayout(2,1), 10, new Color(0xFFFFFF));
        outPanel.setOpaque(false);
        outPanel.setBorder(new EmptyBorder(12,12,12,12));
        bmiLabel.setFont(bmiLabel.getFont().deriveFont(16f));
        calLabel.setFont(calLabel.getFont().deriveFont(16f));
        outPanel.add(bmiLabel);
        outPanel.add(calLabel);
        right.add(outPanel);

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setOpaque(false);
        JButton saveBtn = styledButton("Save Users");
        saveBtn.addActionListener(e -> onSave());
        JButton loadBtn = styledButton("Load Users");
        loadBtn.addActionListener(e -> onLoad());
        filePanel.add(saveBtn); filePanel.add(loadBtn);
        right.add(filePanel);

        // Exercise history section
        initHistoryTable();
        JPanel historyPanel = new RoundedPanel(new BorderLayout(), 10, new Color(0xFFFFFF));
        historyPanel.setBorder(BorderFactory.createTitledBorder(null, "Exercise History", TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14), new Color(45,70,70)));
        historyPanel.setOpaque(false);
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        JPanel histButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        histButtons.setOpaque(false);
        JButton addHistBtn = styledButton("Add Exercise to History");
        addHistBtn.addActionListener(e -> onAddExerciseToHistory());
        JButton clearHistBtn = styledButton("Clear History");
        clearHistBtn.addActionListener(e -> onClearHistory());
        JButton chartBtn = styledButton("Show Calorie Chart");
        chartBtn.addActionListener(e -> onShowChart());
        JButton progressBtn = styledButton("Show Progress");
        progressBtn.addActionListener(e -> onShowProgress());
        JButton goalsBtn = styledButton("Edit Goals");
        goalsBtn.addActionListener(e -> onEditGoals());
        histButtons.add(addHistBtn); histButtons.add(clearHistBtn); histButtons.add(chartBtn);
        histButtons.add(progressBtn); histButtons.add(goalsBtn);
        historyPanel.add(histButtons, BorderLayout.SOUTH);
        right.add(Box.createVerticalStrut(8));
        right.add(historyPanel);

        split.setLeftComponent(left);
        JScrollPane rightScroll = new JScrollPane(right);
        rightScroll.setBorder(null);
        rightScroll.getVerticalScrollBar().setUnitIncrement(20);
        split.setRightComponent(rightScroll);

        add(split);

        userJList.addListSelectionListener(e -> onUserSelected());

        // try loading automatically
        try { loadUsersFromFile(); } catch (Exception ex) { /* ignore */ }

        setVisible(true);
    }

    private void initHistoryTable() {
        historyTableModel = new DefaultTableModel(new Object[]{"Exercise","Calories","Timestamp"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setFillsViewportHeight(true);
        historyTable.setRowHeight(26);
    }

    private void addHistoryRow(ExerciseEntry ee) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        historyTableModel.addRow(new Object[]{ee.getExerciseName(), String.format("%.1f kcal", ee.getCalories()), df.format(new java.util.Date(ee.getTimestamp()))});
    }

    private void clearHistoryTable() {
        historyTableModel.setRowCount(0);
    }

    private void onAddUser(ActionEvent e) {
        String name = nameField.getText().trim();
        int age = parseInt(ageField.getText());
        double weight = parseDouble(weightField.getText());
        double height = parseDouble(heightField.getText());
        String sex = (String) sexBox.getSelectedItem();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }
        // If user exists, update
        for (int i = 0; i < userListModel.size(); i++) {
            User u = userListModel.get(i);
            if (u.getName().equalsIgnoreCase(name)) {
                u.setAge(age); u.setWeightKg(weight); u.setHeightCm(height); u.setSex(sex);
                userJList.repaint();
                return;
            }
        }
        userListModel.addElement(new User(name, age, weight, height, sex));
    }

    private void onUserSelected() {
        User u = userJList.getSelectedValue();
        if (u == null) return;
        nameField.setText(u.getName());
        ageField.setText(String.valueOf(u.getAge()));
        weightField.setText(String.valueOf(u.getWeightKg()));
        heightField.setText(String.valueOf(u.getHeightCm()));
        sexBox.setSelectedItem(u.getSex());
        bmiLabel.setText(String.format("BMI: %.2f", u.calculateBMI()));
        // populate history table
        if (historyTableModel != null) {
            historyTableModel.setRowCount(0);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (ExerciseEntry ee : u.getHistory()) {
                historyTableModel.addRow(new Object[]{ee.getExerciseName(), String.format("%.1f kcal", ee.getCalories()), df.format(new java.util.Date(ee.getTimestamp()))});
            }
        }
        updateGoalPanel();
    }

    // Update Calorie Goal Tracker panel
    private void updateGoalPanel() {
        User u = userJList.getSelectedValue();
        if (u == null) {
            burnedTodayLabel.setText("-");
            remainingLabel.setText("-");
            maintenanceLabel.setText("-");
            mildLabel.setText("-");
            moderateLabel.setText("-");
            return;
        }
        int burned = GoalManager.getCaloriesBurnedToday(u);
        int goal = u.getDailyCalorieGoal();
        int remaining = GoalManager.getRemainingToGoal(u);
        burnedTodayLabel.setText(String.valueOf(burned));
        if (goal > 0) {
            if (remaining <= 0) {
                remainingLabel.setText("Goal achieved! Great job!");
                remainingLabel.setForeground(new Color(0x2E8B57)); // green
            } else {
                remainingLabel.setText(String.valueOf(remaining));
                remainingLabel.setForeground(new Color(0x1F3D3D));
            }
        } else {
            remainingLabel.setText("-");
            remainingLabel.setForeground(new Color(0x1F3D3D));
        }
        int maintenance = GoalManager.getMaintenanceCalories(u);
        maintenanceLabel.setText(String.valueOf(maintenance));
        mildLabel.setText(String.valueOf(GoalManager.getRecommendedIntake(u, 300)));
        moderateLabel.setText(String.valueOf(GoalManager.getRecommendedIntake(u, 500)));
    }
    // Set daily goal from input field
    private void onSetGoal() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        String txt = goalField.getText().trim();
        try {
            int val = Integer.parseInt(txt);
            if (val < 0) throw new NumberFormatException();
            u.setDailyCalorieGoal(val);
            updateGoalPanel();
            JOptionPane.showMessageDialog(this, "Goal updated for " + u.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number for daily goal.");
        }
    }

    private void onCalculateBMI() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        bmiLabel.setText(String.format("BMI: %.2f", u.calculateBMI()));
    }

    private void onEstimateCalories() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        Exercise ex = (Exercise) exerciseBox.getSelectedItem();
        if (ex == null) return;
        double kcal = estimateCaloriesPerHour(ex.getMet(), u.getWeightKg());
        calLabel.setText(String.format("Calories (1h): %.1f kcal", kcal));
    }

    // New: estimate calories using a fixed rate of 8.5 kcal per minute and the user-provided minutes
    private void onEstimateByDuration() {
        String txt = durationField.getText().trim();
        if (txt.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter duration in minutes."); return; }
        double minutes;
        try {
            minutes = Double.parseDouble(txt);
            if (minutes < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid non-negative number for minutes.");
            return;
        }
        double kcal = minutes * 8.5; // fixed calories per minute
        calLabel.setText(String.format("Calories (%.0f min): %.1f kcal", minutes, kcal));
    }

    private void onSave() {
        try {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < userListModel.size(); i++) users.add(userListModel.get(i));
            FitnessDataManager.saveUsers(users, dataFile);
            JOptionPane.showMessageDialog(this, "Saved to: " + dataFile.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void onLoad() {
        try { loadUsersFromFile(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage()); }
    }

    private void onAddExerciseToHistory() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        Exercise ex = (Exercise) exerciseBox.getSelectedItem();
        if (ex == null) { JOptionPane.showMessageDialog(this, "Select an exercise."); return; }
        double kcal = estimateCaloriesPerHour(ex.getMet(), u.getWeightKg());
        ExerciseEntry entry = new ExerciseEntry(ex.getName(), kcal, System.currentTimeMillis());
        u.addExerciseEntry(entry);
        // update UI table
        addHistoryRow(entry);
        updateGoalPanel();
    }

    private void onClearHistory() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Clear exercise history for " + u.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            u.clearHistory();
            clearHistoryTable();
        }
    }

    private void onShowChart() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        java.util.Map<String, Double> data = u.aggregateCaloriesPerExercise();
        ChartWindow cw = new ChartWindow(u.getName(), data);
        cw.setVisible(true);
    }

    private void onShowProgress() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        ProgressWindow pw = new ProgressWindow(u);
        pw.setVisible(true);
    }

    private void onEditGoals() {
        User u = userJList.getSelectedValue();
        if (u == null) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        GoalsDialog gd = new GoalsDialog(this, u);
        gd.setVisible(true);
        if (gd.isSaved()) {
            JOptionPane.showMessageDialog(this, "Goals updated for " + u.getName());
        }
    }

    private void loadUsersFromFile() throws Exception {
        List<User> users = FitnessDataManager.loadUsers(dataFile);
        userListModel.clear();
        for (User u : users) userListModel.addElement(u);
    }

    // MET-based calculation using standard formula: kcal/min = (MET * 3.5 * weightKg) / 200
    // For 60 minutes multiply by 60
    private double estimateCaloriesPerHour(double met, double weightKg) {
        return met * 3.5 * weightKg / 200.0 * 60.0;
    }

    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; } }
    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; } }

    private List<Exercise> defaultExercises() {
        List<Exercise> list = new ArrayList<>();
        list.add(new Exercise("Walking (3.0 mph)", 3.3));
        list.add(new Exercise("Jogging (6 mph)", 10.0));
        list.add(new Exercise("Running (8 mph)", 11.8));
        list.add(new Exercise("Cycling (moderate)", 8.0));
        list.add(new Exercise("Swimming (moderate)", 6.0));
        list.add(new Exercise("Yoga (Hatha)", 2.5));
        list.add(new Exercise("Strength training (moderate)", 6.0));
        return list;
    }

    // Helper to create a styled rounded button
    private JButton styledButton(String text) {
        JButton b = new RoundedButton(text);
        b.setBackground(new Color(0xB3D9FF)); // pastel blue
        b.setForeground(new Color(0x1F3D3D));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        return b;
    }

    // RoundedPanel - lightweight rounded background panel used for grouping sections
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(LayoutManager lm, int radius, Color bg) {
            super(lm);
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        public RoundedPanel(LayoutManager lm) { this(lm, 12, new Color(0xFFFFFF)); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // RoundedButton for consistent modern buttons
    private static class RoundedButton extends JButton {
        private static final int R = 14;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), R, R);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void paintBorder(Graphics g) { /* no border */ }
    }
}
