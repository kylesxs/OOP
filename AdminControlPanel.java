import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class AdminControlPanel {
    private JFrame frame;
    private Map<String, PC> pcs;
    private JPanel pcStatusPanel;
    private Timer updateTimer;
    private static final String FILE_NAME = "pc_data.ser";

    public AdminControlPanel() {
        pcs = loadPCData();
        if (pcs == null) {
            pcs = new HashMap<>();
            for (int i = 1; i <= 10; i++) {
                pcs.put("PC" + i, new PC("PC" + i));
            }
        } else {
            pcs.values().forEach(PC::resumeTimer); // Resume timers for all PCs
        }

        frame = new JFrame("Admin Control Panel - Kyujo Internet Cafe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        pcStatusPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // Vertical layout with gaps
        JScrollPane scrollPane = new JScrollPane(pcStatusPanel);

        updateStatus();

        // Adding Back button
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.addActionListener(e -> {
            if (updateTimer != null) {
                updateTimer.stop();
            }
            savePCData();
            new MainMenu().showMainMenu();
            frame.dispose();
        });

        mainPanel.add(backButton, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);

        startUpdateTimer();
    }

    private void updateStatus() {
        pcStatusPanel.removeAll();
        for (int i = 1; i <= pcs.size(); i++) {
            PC pc = pcs.get("PC" + i);
            JPanel pcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Left-aligned with gaps
            JLabel statusLabel = new JLabel(pc.getName() + " - " + pc.getStatus());
            JLabel userLabel = new JLabel(pc.getUser());
            JProgressBar progressBar = new JProgressBar(0, pc.getOriginalUsageTimeSeconds());
            progressBar.setValue(pc.getOriginalUsageTimeSeconds() - pc.getUsageTimeSeconds());
            progressBar.setStringPainted(true);
    
            JButton startButton = new JButton("Start");
            JButton stopButton = new JButton("Stop");
            JButton manageButton = new JButton("Manage Time");
            JButton assignUserButton = new JButton("Assign User");
    
            startButton.addActionListener(e -> {
                pc.start();
                updateStatus();
            });
    
            stopButton.addActionListener(e -> {
                pc.stop(); // Now the stop method will handle showing the receipt
                updateStatus();
            });
    
            manageButton.addActionListener(e -> showManageTimeDialog(pc));
    
            assignUserButton.addActionListener(e -> showAssignUserDialog(pc));
    
            pcPanel.add(statusLabel);
            pcPanel.add(userLabel);
            pcPanel.add(progressBar);
            pcPanel.add(startButton);
            pcPanel.add(stopButton);
            pcPanel.add(manageButton);
            pcPanel.add(assignUserButton);
    
            pcStatusPanel.add(pcPanel);
        }
        pcStatusPanel.revalidate();
        pcStatusPanel.repaint();
    }
    

    private void showManageTimeDialog(PC pc) {
        JDialog dialog = new JDialog(frame, "Manage Time for " + pc.getName(), true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        JLabel hoursLabel = new JLabel("Hours:");
        JTextField hoursField = new JTextField();
        JLabel minutesLabel = new JLabel("Minutes:");
        JTextField minutesField = new JTextField();
        JLabel secondsLabel = new JLabel("Seconds:");
        JTextField secondsField = new JTextField();
        JButton setTimeButton = new JButton("Set Time");

        setTimeButton.addActionListener(e -> {
            int hours = Integer.parseInt(hoursField.getText());
            int minutes = Integer.parseInt(minutesField.getText());
            int seconds = Integer.parseInt(secondsField.getText());
            pc.setUsageTime(hours, minutes, seconds);
            dialog.dispose();
            updateStatus();
        });

        panel.add(hoursLabel);
        panel.add(hoursField);
        panel.add(minutesLabel);
        panel.add(minutesField);
        panel.add(secondsLabel);
        panel.add(secondsField);
        panel.add(setTimeButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAssignUserDialog(PC pc) {
        JDialog dialog = new JDialog(frame, "Assign User to " + pc.getName(), true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JLabel userLabel = new JLabel("Enter User Name:");
        JTextField userField = new JTextField();
        JButton assignButton = new JButton("Assign");

        assignButton.addActionListener(e -> {
            String userName = userField.getText();
            pc.setUser(userName);
            dialog.dispose();
            updateStatus();
        });

        panel.add(userLabel);
        panel.add(userField);
        panel.add(assignButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(1000, e -> updateStatus());
        updateTimer.start();
    }

    private void savePCData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(pcs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, PC> loadPCData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Map<String, PC>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate and display receipt when the time is stopped
    private void showReceipt(PC pc) {
        int usageTime = pc.getOriginalUsageTimeSeconds() - pc.getUsageTimeSeconds();
        int hours = usageTime / 3600;
        int minutes = (usageTime % 3600) / 60;
        int seconds = usageTime % 60;

        String receiptMessage = String.format(
            "Receipt for %s\nUser: %s\nPC: %s\nTotal Usage Time: %02d:%02d:%02d",
            pc.getName(), pc.getUser(), pc.getName(), hours, minutes, seconds
        );

        JOptionPane.showMessageDialog(frame, receiptMessage, "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
}
