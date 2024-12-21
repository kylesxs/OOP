import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PC implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String status;
    private int usageTimeSeconds; // Usage time in seconds
    private int originalUsageTimeSeconds; // Original usage time in seconds
    private String user;
    private transient Timer timer; // Transient to avoid serialization issues
    private LocalDateTime endTime; // The end time for the usage period

    public PC(String name) {
        this.name = name;
        this.status = "Available";
        this.user = "No User Assigned";
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status + " (" + formatTime(usageTimeSeconds) + ")";
    }

    public int getUsageTimeSeconds() {
        return usageTimeSeconds;
    }

    public int getOriginalUsageTimeSeconds() {
        return originalUsageTimeSeconds;
    }

    public void start() {
        this.status = "In Use";
        startTimer();
    }

    public void stop() {
        this.status = "Available";
        // Create receipt data when stopping
        String receipt = generateReceipt();
        JOptionPane.showMessageDialog(null, receipt, "Receipt", JOptionPane.INFORMATION_MESSAGE);

        this.usageTimeSeconds = 0;
        this.originalUsageTimeSeconds = 0; // Reset original usage time
        this.user = "No User Assigned"; // Reset user when stopped
        this.endTime = null;
        if (timer != null) {
            timer.stop();
        }
    }

    public void setUsageTime(int hours, int minutes, int seconds) {
        this.usageTimeSeconds = hours * 3600 + minutes * 60 + seconds;
        this.originalUsageTimeSeconds = this.usageTimeSeconds; // Set the original usage time
        this.endTime = LocalDateTime.now().plusSeconds(this.usageTimeSeconds); // Set end time
        startTimer(); // Start the timer when setting the usage time
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() { // Timer updates every second
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usageTimeSeconds > 0) {
                    usageTimeSeconds--;
                } else {
                    stop();
                    ((Timer)e.getSource()).stop(); // Ensure the timer stops when usage time is up
                }
            }
        });
        timer.start();
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void resumeTimer() {
        if (endTime != null) {
            long remainingTime = LocalDateTime.now().until(endTime, ChronoUnit.SECONDS);
            if (remainingTime > 0) {
                this.usageTimeSeconds = (int) remainingTime;
                startTimer();
            } else {
                stop();
            }
        }
    }

    // Generate a receipt with pricing
    private String generateReceipt() {
        int usageTime = this.originalUsageTimeSeconds - this.usageTimeSeconds;
        int hours = usageTime / 3600;
        int minutes = (usageTime % 3600) / 60;
        int seconds = usageTime % 60;

        // Calculate the price based on usage time
        int totalPrice = calculatePrice(usageTime);

        // Return a formatted string with the user, usage time, and price
        return String.format(
            "Receipt for %s\nUser: %s\nPC: %s\nTotal Usage Time: %02d:%02d:%02d\nTotal Price: ₱%d",
            this.name, this.user, this.name, hours, minutes, seconds, totalPrice
        );
    }

    // Calculate the price based on usage time
    private int calculatePrice(int usageTime) {
        // Calculate the price for every 1 hour (₱12 per hour)
        int hours = (int) Math.ceil(usageTime / 3600.0); // Round up to the nearest hour

        // If usage time is less than or equal to 30 minutes, charge half the price
        if (usageTime <= 1800) {
            return 6; // ₱6 for 30 minutes
        } else {
            return hours * 12; // ₱12 per hour
        }
    }
}
