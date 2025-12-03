package fitnesstracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        super("Welcome");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 460);
        setLocationRelativeTo(null);

        // Create main gradient panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                // Pastel palette requested
                Color pastelGreen = new Color(0xAEE6CE);
                Color pastelBlue = new Color(0xB3D9FF);
                GradientPaint gp = new GradientPaint(0, 0, pastelGreen, w, h, pastelBlue);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Soft rounded panel in center
                int pw = (int)(w * 0.78);
                int ph = (int)(h * 0.6);
                int px = (w - pw) / 2;
                int py = (h - ph) / 2;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(px, py, pw, ph, 28, 28);

                g2.dispose();
            }
        };
        gradientPanel.setLayout(new GridBagLayout());

        // Content panel (transparent) so background shows
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to Wellness Tracker");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(new Color(28, 60, 60));

        JLabel subtitle = new JLabel("Your journey to better health starts here.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(70, 100, 100));

        content.add(Box.createVerticalStrut(20));
        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(28));

        JButton cont = new RoundedButton("Continue");
        cont.setAlignmentX(Component.CENTER_ALIGNMENT);
        cont.setPreferredSize(new Dimension(160, 44));
        cont.setMaximumSize(new Dimension(220, 56));
        cont.setBackground(new Color(0x7CC8B4));
        cont.setFocusPainted(false);
        cont.setFont(new Font("SansSerif", Font.BOLD, 16));
        cont.setForeground(Color.white);
        cont.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cont.addActionListener(this::onContinue);

        // Add some decorative rounded shapes (as lightweight panels)
        content.add(cont);
        content.add(Box.createVerticalStrut(8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        gradientPanel.add(content, gbc);

        add(gradientPanel);
        setMinimumSize(new Dimension(480, 320));
    }

    private void onContinue(ActionEvent ev) {
        // Open main GUI and dispose welcome
        SwingUtilities.invokeLater(() -> {
            new GUI();
        });
        dispose();
    }

    public static void showAndWait() {
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen ws = new WelcomeScreen();
            ws.setVisible(true);
        });
    }

    // Rounded button implementation for a modern look
    private static class RoundedButton extends JButton {
        private static final int R = 18;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), R, R);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {
            // no border
        }

        @Override
        public boolean isContentAreaFilled() {
            return false;
        }
    }
}
