import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen {
    public void showLoginScreen() {
        // Create the frame
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        // Create the panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Add username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        JTextField usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(usernameField, constraints);

        // Add password label and text field
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        JPasswordField passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(passwordField, constraints);

        // Add toggle button to show/hide password
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(showPasswordCheckBox, constraints);

        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            }
        });

        // Add login button
        JButton loginButton = new JButton("Login");
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(loginButton, constraints);

        frame.add(panel);
        frame.setVisible(true);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                // Set default username and password
                String defaultUsername = "Admin";
                String defaultPassword = "12345";

                // Check if the entered username and password match the defaults
                if (username.equals(defaultUsername) && String.valueOf(password).equals(defaultPassword)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();
                    new MainMenu().showMainMenu();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                }
            }
        });
    }
}
