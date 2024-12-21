import javax.swing.*;
import java.awt.*;

public class ComputerShop {

    public void showComputerShop() {
        JFrame frame = new JFrame("Kyujo Internet Cafe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Kyujo Internet Cafe!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel servicesPanel = new JPanel(new GridLayout(1, 1, 10, 10)); // Adjusted to only one row for the button
        servicesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton adminControlPanelButton = new JButton("Admin Control Panel");
        adminControlPanelButton.setFont(new Font("Arial", Font.PLAIN, 18));
        servicesPanel.add(adminControlPanelButton);

        adminControlPanelButton.addActionListener(e -> {
            new AdminControlPanel();
            frame.dispose();
        });

        mainPanel.add(servicesPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.addActionListener(e -> {
            new MainMenu().showMainMenu();
            frame.dispose();
        });
        mainPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
