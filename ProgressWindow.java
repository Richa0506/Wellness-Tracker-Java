package fitnesstracker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ProgressWindow displays daily and weekly calorie trends derived from user's exercise history.
 * It shows the last 7 days as a bar chart and a goal indicator line.
 */
public class ProgressWindow extends JFrame {
    private final User user;
    private final ChartPanel chartPanel;

    public ProgressWindow(User user) {
        super("Progress - " + (user != null ? user.getName() : "User"));
        this.user = user;
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(0xF3FBF8));

        setLayout(new BorderLayout());

        chartPanel = new ChartPanel();
        add(chartPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(8,12,12,12));

        JButton refresh = new RoundedButton("Refresh");
        refresh.setBackground(new Color(0xB3D9FF));
        refresh.addActionListener(e -> refresh());
        JButton close = new RoundedButton("Close");
        close.setBackground(new Color(0xAEE6CE));
        close.addActionListener(e -> dispose());
        bottom.add(refresh);
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        // Build last 7 days data
        Map<LocalDate, Double> map = user.getDailyTotalsMap();
        LocalDate today = LocalDate.now();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("E");
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            labels.add(d.format(df));
            values.add(map.getOrDefault(d, 0.0));
        }
        double goal = user.getDailyCalorieGoal();
        chartPanel.setData(labels, values, goal);
        chartPanel.repaint();
    }

    // Inner chart panel draws pastel bar chart and goal line
    private static class ChartPanel extends JPanel {
        private List<String> labels = new ArrayList<>();
        private List<Double> values = new ArrayList<>();
        private double goal = 0.0;

        public ChartPanel() {
            setBackground(new Color(0xF3FBF8));
            setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        }

        public void setData(List<String> labels, List<Double> values, double goal) {
            this.labels = labels;
            this.values = values;
            this.goal = goal;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw title
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.setColor(new Color(28,60,60));
            g2.drawString("Daily Calories (last 7 days)", 12, 22);

            if (values == null || values.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString("No activity data available.", 12, 48);
                g2.dispose();
                return;
            }

            double max = 1; for (Double v : values) if (v > max) max = v;
            if (goal > max) max = goal;

            int left = 40, top = 60, bottom = h - 60;
            int plotH = bottom - top;
            int plotW = w - left - 40;

            int n = values.size();
            int gap = 12;
            int barW = Math.max(20, (plotW - (n+1)*gap) / Math.max(1, n));

            // draw background panel
            g2.setColor(new Color(255,255,255,200));
            g2.fillRoundRect(12, 40, w-24, h-88, 14, 14);

            // draw goal line
            if (goal > 0) {
                int gy = bottom - (int) Math.round((goal/max) * plotH);
                g2.setColor(new Color(120,200,180,180));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, new float[]{6f,6f}, 0f));
                g2.drawLine(left, gy, left+plotW, gy);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(new Color(28,60,60));
                g2.drawString("Goal: " + String.format("%.0f kcal", goal), left+plotW-100, gy-6);
            }

            // pastel palette
            Color[] palette = new Color[]{ new Color(0xAEE6CE), new Color(0xB3D9FF), new Color(0x7CC8B4) };

            int x = left + gap;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (int i = 0; i < n; i++) {
                double val = values.get(i);
                int barH = (int) Math.round((val / max) * (plotH - 10));
                int bx = x + i * (barW + gap);
                int by = bottom - barH;

                Color c = palette[i % palette.length];
                g2.setColor(c);
                g2.fillRoundRect(bx, by, barW, barH, 8, 8);

                // value label
                g2.setColor(new Color(28,60,60));
                String valStr = String.format("%.0f", val);
                int vsW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, bx + (barW - vsW)/2, by - 6);

                // x-label
                String lbl = labels.get(i);
                int lblW = g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, bx + (barW - lblW)/2, bottom + 16);
            }

            g2.dispose();
        }
    }

    // Local RoundedButton implementation for consistent UI
    private static class RoundedButton extends JButton {
        private static final int R = 14;
        public RoundedButton(String text) { super(text); setContentAreaFilled(false); setFocusPainted(false); setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), R, R);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public void paintBorder(Graphics g) { }
    }
}
