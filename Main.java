package fitnesstracker;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Initialize a modern Look & Feel (FlatLaf preferred). Fall back to system L&F.
        initLookAndFeel();

        // Show welcome screen first; it will open the main GUI when Continue is clicked.
        WelcomeScreen.showAndWait();
    }

    private static void initLookAndFeel() {
        try {
            // Try FlatLaf if available on classpath
            Class.forName("com.formdev.flatlaf.FlatLightLaf");
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // If all else fails, keep default L&F
            }
        }

        // Apply some global font defaults for a modern, clean look
        Font defaultFont = new Font("SansSerif", Font.PLAIN, 14);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont.deriveFont(Font.BOLD, 14f));
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("List.font", defaultFont);
    }
}
