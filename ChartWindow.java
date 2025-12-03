package fitnesstracker;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Simple chart window that draws a bar chart showing calories per exercise.
 * This avoids external chart libraries and uses custom painting to match the pastel theme.
 */
public class ChartWindow extends JFrame {
    public ChartWindow(String userName, Map<String, Double> data) {
        super("Calories Burned by Exercise - " + userName);
        setSize(720, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(new Color(0xF3FBF8));

        setLayout(new BorderLayout());
        ChartPanel cp = new ChartPanel(data);
        add(cp, BorderLayout.CENTER);

        // Bottom control panel with a 'Go Back' button that closes this window and returns focus to main GUI
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JButton backBtn = new RoundedButton("Go Back");
        backBtn.setBackground(new Color(0xAEE6CE)); // pastel green
        backBtn.setForeground(new Color(0x1F3D3D));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backBtn.addActionListener(ev -> {
            // Close only this chart window; do not exit the whole application
            dispose();
        });

        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    // RoundedButton for consistent modern buttons within ChartWindow
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

    private static class ChartPanel extends JPanel {
        private final java.util.List<String> keys = new java.util.ArrayList<>();
        private final java.util.List<Double> values = new java.util.ArrayList<>();

        public ChartPanel(Map<String, Double> data) {
            setBackground(new Color(0xF3FBF8));
            if (data != null) {
                data.forEach((k, v) -> { keys.add(k); values.add(v); });
            }
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // draw title
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.setColor(new Color(28, 60, 60));
            g2.drawString("Calories Burned by Exercise", 20, 28);

            if (keys.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.drawString("No exercise history to display.", 20, 60);
                g2.dispose();
                return;
            }

            double max = 0;
            for (Double v : values) if (v > max) max = v;
            if (max <= 0) max = 1;

            int left = 60, top = 60, bottom = h - 80;
            int plotH = bottom - top;
            int plotW = w - left - 40;

            int n = keys.size();
            int gap = 16;
            int barW = Math.max(24, (plotW - (n + 1) * gap) / Math.max(1, n));

            // draw axis
            g2.setColor(new Color(180, 200, 190));
            g2.fillRoundRect(left - 40, top - 10, plotW + 80, plotH + 20, 12, 12);

            // pastel palette for bars
            Color[] palette = new Color[] { new Color(0xAEE6CE), new Color(0xB3D9FF), new Color(0x7CC8B4), new Color(0xFFD9B3), new Color(0xE6C7FF) };

            int x = left + gap;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            for (int i = 0; i < n; i++) {
                double val = values.get(i);
                int barH = (int) Math.round((val / max) * (plotH - 20));
                int bx = x + i * (barW + gap);
                int by = bottom - barH;

                Color c = palette[i % palette.length];
                g2.setColor(c);
                g2.fillRoundRect(bx, by, barW, barH, 8, 8);

                // label
                g2.setColor(new Color(28, 60, 60));
                String lbl = keys.get(i);
                int strW = g2.getFontMetrics().stringWidth(lbl);
                int lx = bx + (barW - strW) / 2;
                g2.drawString(lbl, Math.max(10, lx), bottom + 16);

                // value on top
                String valStr = String.format("%.0f", val);
                int vsW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, bx + (barW - vsW) / 2, by - 8);
            }

            g2.dispose();
        }
    }
}
