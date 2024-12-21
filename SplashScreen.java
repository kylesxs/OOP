import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JWindow {
    private int duration;

    public SplashScreen(int duration) {
        this.duration = duration;
    }

    public void showSplash() {
        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.white);

        // Set the window's bounds, position the window in the center of the screen
        int width = 600;
        int height = 500;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        // Build the splash screen
        JLabel label = new JLabel(new ImageIcon("Kyujo-Internet-Cafe.PNG"));
        content.add(label, BorderLayout.CENTER);

        // Create the fade-out animation
        Timer timer = new Timer(30, new ActionListener() {
            private float opacity = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.03f;
                if (opacity <= 0) {
                    ((Timer) e.getSource()).stop();
                    setVisible(false);
                    dispose();
                    // Show login screen after splash screen
                    new LoginScreen().showLoginScreen();
                } else {
                    setOpacity(opacity);
                }
            }
        });

        // Display it
        setVisible(true);

        // Wait a little while doing nothing, then start the fade-out animation
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer.start();
    }
}
