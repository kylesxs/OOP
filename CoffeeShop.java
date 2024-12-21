import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoffeeShop {
    private List<Product> products;
    private JTextArea receiptArea;
    private JLabel drinksCostLabel, cakesCostLabel, serviceChargeLabel, taxLabel, subtotalLabel, totalLabel;
    private double drinksCost = 0.0, cakesCost = 0.0, serviceCharge = 5.0, tax = 0.0, subtotal = 0.0, total = 0.0;
    private List<OrderItem> orderHistory; // To keep track of added products
    private static final String STOCK_FILE = "stock_data.txt";
    private List<String> receiptLines; // To track receipt lines for cancellation

    public CoffeeShop() {
        products = new ArrayList<>();
        orderHistory = new ArrayList<>();
        receiptLines = new ArrayList<>(); // Initialize the receipt lines
        // Add some initial products
        products.add(new Product("Latte", 4.5, 10));
        products.add(new Product("Espresso", 3.0, 15));
        products.add(new Product("Cappuccino", 4.0, 12));
        products.add(new Product("Cheesecake", 5.0, 8));
        products.add(new Product("Muffin", 2.5, 20));

        loadStock(); // Load stock from file when the CoffeeShop is initialized
    }

    // Load product stock from a file
    private void loadStock() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STOCK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String productName = parts[0];
                int stock = Integer.parseInt(parts[1]);

                // Find the product and update its stock
                for (Product product : products) {
                    if (product.getName().equalsIgnoreCase(productName)) {
                        product.setStock(stock);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading stock data. Using default stock values.");
        }
    }

    // Save the product stock to a file
    private void saveStock() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_FILE))) {
            for (Product product : products) {
                writer.write(product.getName() + "," + product.getStock());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving stock data.");
        }
    }

    public void showCoffeeShop() {
        JFrame frame = new JFrame("Coffee Shop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left panel for products
        JPanel productsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        for (Product product : products) {
            JButton productButton = new JButton(product.getName() + " ($" + product.getPrice() + ")");
            productButton.setPreferredSize(new Dimension(200, 50));
            productButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showProductWindow(product);
                }
            });
            productsPanel.add(productButton, constraints);
            constraints.gridy++;
        }

        // Add Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStock(); // Save stock before going back
                new MainMenu().showMainMenu();
                frame.dispose();
            }
        });
        constraints.gridx = 0;
        constraints.gridy++;
        productsPanel.add(backButton, constraints);

        // Add Cancel Order button
        JButton cancelButton = new JButton("Cancel Last Item");
        cancelButton.setPreferredSize(new Dimension(200, 50));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelLastItem();
            }
        });
        constraints.gridy++;
        productsPanel.add(cancelButton, constraints);

        // Add Checkout button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setPreferredSize(new Dimension(200, 50));
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkout();
            }
        });
        constraints.gridy++;
        productsPanel.add(checkoutButton, constraints);

        // Right panel for receipt and costs
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));

        receiptArea = new JTextArea(20, 25);
        receiptArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptArea);

        drinksCostLabel = new JLabel("Cost of Drinks: $0.00");
        cakesCostLabel = new JLabel("Cost of Cakes: $0.00");
        serviceChargeLabel = new JLabel("Service Charge: $5.00");
        taxLabel = new JLabel("Tax: $0.00");
        subtotalLabel = new JLabel("Subtotal: $0.00");
        totalLabel = new JLabel("Total: $0.00");

        receiptPanel.add(scrollPane);
        receiptPanel.add(drinksCostLabel);
        receiptPanel.add(cakesCostLabel);
        receiptPanel.add(serviceChargeLabel);
        receiptPanel.add(taxLabel);
        receiptPanel.add(subtotalLabel);
        receiptPanel.add(totalLabel);

        mainPanel.add(productsPanel, BorderLayout.WEST);
        mainPanel.add(receiptPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void showProductWindow(Product product) {
        JFrame productFrame = new JFrame(product.getName());
        productFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productFrame.setSize(300, 200);
        productFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel nameLabel = new JLabel("Product: " + product.getName());
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(nameLabel, constraints);

        JLabel stockLabel = new JLabel("Stock: " + product.getStock());
        constraints.gridy = 1;
        panel.add(stockLabel, constraints);

        JLabel quantityLabel = new JLabel("Quantity:");
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        panel.add(quantityLabel, constraints);

        JTextField quantityField = new JTextField(5);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(quantityField, constraints);

        JButton checkoutButton = new JButton("Add to Cart");
        constraints.gridy = 3;
        constraints.gridx = 1;
        panel.add(checkoutButton, constraints);

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0 && quantity <= product.getStock()) {
                    product.setStock(product.getStock() - quantity); // Update stock immediately
                    updateCosts(product, quantity);
                    orderHistory.add(new OrderItem(product, quantity)); // Save the product to order history
                    productFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(productFrame, "Invalid quantity. Please enter a value between 1 and " + product.getStock());
                }
            }
        });

        productFrame.add(panel);
        productFrame.setVisible(true);
    }

    private void updateCosts(Product product, int quantity) {
        double cost = product.getPrice() * quantity;
        if (product.getName().contains("Latte") || product.getName().contains("Espresso") || product.getName().contains("Cappuccino")) {
            drinksCost += cost;
        } else {
            cakesCost += cost;
        }

        tax = (drinksCost + cakesCost + serviceCharge) * 0.05;
        subtotal = drinksCost + cakesCost + serviceCharge;
        total = subtotal + tax;

        DecimalFormat df = new DecimalFormat("#.##");
        drinksCostLabel.setText("Cost of Drinks: $" + df.format(drinksCost));
        cakesCostLabel.setText("Cost of Cakes: $" + df.format(cakesCost));
        serviceChargeLabel.setText("Service Charge: $5.00");
        taxLabel.setText("Tax: $" + df.format(tax));
        subtotalLabel.setText("Subtotal: $" + df.format(subtotal));
        totalLabel.setText("Total: $" + df.format(total));

        updateReceipt(product, quantity, cost);
    }

    private void updateReceipt(Product product, int quantity, double cost) {
        String line = quantity + " x " + product.getName() + " @ $" + product.getPrice() + " each = $" + cost;
        receiptLines.add(line); // Store receipt lines
        receiptArea.append(line + "\n");
        receiptArea.append("Tax: $" + new DecimalFormat("#.##").format(tax) + "\n");
        receiptArea.append("Subtotal: $" + new DecimalFormat("#.##").format(subtotal) + "\n");
        receiptArea.append("Total: $" + new DecimalFormat("#.##").format(total) + "\n");
        receiptArea.append("---------------------------------\n");
    }

    private void cancelLastItem() {
        if (!orderHistory.isEmpty()) {
            OrderItem lastItem = orderHistory.remove(orderHistory.size() - 1); // Remove last item
            Product product = lastItem.getProduct();
            int quantity = lastItem.getQuantity();
            product.setStock(product.getStock() + quantity); // Restore the stock

            // Update costs and receipt
            updateCosts(product, -quantity);
            removeLastReceiptLine(); // Remove last line from receipt
        }
    }

    private void removeLastReceiptLine() {
        if (!receiptLines.isEmpty()) {
            receiptLines.remove(receiptLines.size() - 1); // Remove the last added receipt line
            receiptArea.setText(""); // Clear receipt area
            for (String line : receiptLines) {
                receiptArea.append(line + "\n"); // Re-add remaining lines
            }
        }
    }

    private void checkout() {
        if (orderHistory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items in the cart to checkout.");
            return;
        }

        // Finalize checkout (optional: show a final receipt or confirmation)
        JOptionPane.showMessageDialog(null, "Checkout successful!");
        orderHistory.clear(); // Clear the order history after checkout
        saveStock(); // Save stock after checkout
        receiptArea.setText(""); // Clear the receipt
    }

    // Inner class to store order details
    class OrderItem {
        private Product product;
        private int quantity;

        public OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
