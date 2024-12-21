import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    public void showMainMenu() {
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        JButton computerShopButton = new JButton("Computer Shop");
        computerShopButton.setPreferredSize(new Dimension(200, 50));
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(computerShopButton, constraints);

        JButton coffeeShopButton = new JButton("Coffee Shop");
        coffeeShopButton.setPreferredSize(new Dimension(200, 50));
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(coffeeShopButton, constraints);

        computerShopButton.addActionListener(e -> {
            new AdminControlPanel();
            frame.dispose();
        });

        coffeeShopButton.addActionListener(e -> {
            new CoffeeShop().showCoffeeShop();
            frame.dispose();
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
