package fitnesstracker;

import javax.swing.*;
import java.awt.*;

/**
 * Simple dialog to edit a user's daily calorie goal and weight goal.
 */
public class GoalsDialog extends JDialog {
    private JTextField dailyGoalField;
    private JTextField weightGoalField;
    private boolean saved = false;

    public GoalsDialog(JFrame owner, User user) {
        super(owner, "Edit Goals - " + (user != null ? user.getName() : "User"), true);
        setSize(360, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Daily Calorie Goal (kcal):"), c);
        c.gridx = 1; dailyGoalField = new JTextField(10); form.add(dailyGoalField, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Weight Goal (kg):"), c);
        c.gridx = 1; weightGoalField = new JTextField(10); form.add(weightGoalField, c);

        // populate with current values
        if (user != null) {
            dailyGoalField.setText(String.format("%d", user.getDailyCalorieGoal()));
            weightGoalField.setText(String.format("%.1f", user.getWeightGoalKg()));
        }

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        save.addActionListener(e -> {
            // validate and set
            try {
                int dg = Integer.parseInt(dailyGoalField.getText().trim());
                double wg = Double.parseDouble(weightGoalField.getText().trim());
                if (user != null) {
                    user.setDailyCalorieGoal(dg);
                    user.setWeightGoalKg(wg);
                }
                saved = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for goals.");
            }
        });
        cancel.addActionListener(e -> dispose());
        buttons.add(save); buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);
    }

    public boolean isSaved() { return saved; }
}
