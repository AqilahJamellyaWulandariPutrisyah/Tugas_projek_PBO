package ui;

import db.Database;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class BuyTicketFrame extends JFrame {
    private JTextField concertIdField = new JTextField(5);
    private JTextField ticketCountField = new JTextField(5);
    private JComboBox<String> sourceCombo = new JComboBox<>(new String[]{"From Concert", "From Selling"});
    private JButton buyBtn = new JButton("Add to Cart");
    private int userId;
    private UserDashboard parent;

    public BuyTicketFrame(int userId, UserDashboard parent) {
        this.userId = userId;
        this.parent = parent;
        System.out.println("BuyTicketFrame initialized with userId: " + userId + ", parent: " + (parent != null ? "not null" : "null"));

        // Set cross-platform look and feel to match AdminDashboard and UserDashboard
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Failed to set Cross-Platform Look and Feel: " + ex.getMessage());
        }

        setTitle("Buy Ticket");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 300);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 245, 255), 0, getHeight(), new Color(200, 220, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Font for labels and inputs
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Row 1: Concert ID
        JLabel concertIdLabel = new JLabel("Concert ID:");
        concertIdLabel.setFont(labelFont);
        concertIdLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(concertIdLabel, gbc);

        concertIdField.setFont(inputFont);
        concertIdField.setBorder(new RoundedBorder(10));
        concertIdField.setBackground(new Color(255, 255, 255));
        concertIdField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(concertIdField, gbc);

        // Row 2: Tickets
        JLabel ticketCountLabel = new JLabel("Tickets:");
        ticketCountLabel.setFont(labelFont);
        ticketCountLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(ticketCountLabel, gbc);

        ticketCountField.setFont(inputFont);
        ticketCountField.setBorder(new RoundedBorder(10));
        ticketCountField.setBackground(new Color(255, 255, 255));
        ticketCountField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(ticketCountField, gbc);

        // Row 3: Source
        JLabel sourceLabel = new JLabel("Source:");
        sourceLabel.setFont(labelFont);
        sourceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(sourceLabel, gbc);

        sourceCombo.setFont(inputFont);
        sourceCombo.setBackground(new Color(255, 255, 255));
        sourceCombo.setBorder(new RoundedBorder(10));
        sourceCombo.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(sourceCombo, gbc);

        // Row 4: Buy Button
        buyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buyBtn.setBackground(new Color(33, 150, 243));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setBorder(new RoundedBorder(12));
        buyBtn.setFocusPainted(false);
        buyBtn.setOpaque(true);
        buyBtn.setContentAreaFilled(true);
        buyBtn.setBorderPainted(true);
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buyBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                Color bg = model.isRollover() ? new Color(25, 118, 210) : new Color(33, 150, 243);
                g.setColor(bg);
                g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g, c);
            }
        });
        buyBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buyBtn.setBackground(new Color(25, 118, 210));
                buyBtn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buyBtn.setBackground(new Color(33, 150, 243));
                buyBtn.repaint();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buyBtn, gbc);

        add(mainPanel);

        buyBtn.addActionListener(e -> addToCart());
        setLocationRelativeTo(null);
    }

    // Custom rounded border class
    private static class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        public boolean isBorderOpaque() {
            return false;
        }
    }

    private void addToCart() {
        try {
            System.out.println("Starting addToCart process...");
            String concertIdText = concertIdField.getText().trim();
            String ticketCountText = ticketCountField.getText().trim();
            if (concertIdText.isEmpty() || ticketCountText.isEmpty()) {
                throw new IllegalArgumentException("Concert ID and ticket count cannot be empty.");
            }

            int concertId = Integer.parseInt(concertIdText);
            int ticketsToBuy = Integer.parseInt(ticketCountText);
            System.out.println("Parsed inputs: concertId=" + concertId + ", ticketsToBuy=" + ticketsToBuy);

            if (ticketsToBuy <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number of tickets (greater than 0).");
                return;
            }

            String source = (String) sourceCombo.getSelectedItem();
            System.out.println("Selected source: " + source);

            try (Connection con = Database.getConnection()) {
                if (con == null) {
                    throw new SQLException("Database connection is null!");
                }
                System.out.println("Database connection established.");

                String concertName = "";
                double price = 0.0;
                int availableTickets = 0;

                if ("From Concert".equals(source)) {
                    System.out.println("Processing addition from concert stock...");
                    PreparedStatement checkPs = con.prepareStatement(
                        "SELECT tickets_available, price, name FROM concerts WHERE id = ? FOR UPDATE");
                    checkPs.setInt(1, concertId);
                    ResultSet rs = checkPs.executeQuery();
                    if (!rs.next()) {
                        throw new SQLException("Concert ID " + concertId + " not found!");
                    }
                    availableTickets = rs.getInt("tickets_available");
                    price = rs.getDouble("price");
                    concertName = rs.getString("name");
                    System.out.println("Concert price: " + price + ", Available tickets: " + availableTickets);

                    if (availableTickets < ticketsToBuy) {
                        throw new SQLException("Not enough tickets available! Available: " + availableTickets + ", Requested: " + ticketsToBuy);
                    }
                } else {
                    System.out.println("Processing addition from selling table...");
                    PreparedStatement checkPs = con.prepareStatement(
                        "SELECT id, user_id, tickets_for_sale, price_per_ticket, (SELECT name FROM concerts WHERE id = selling.concert_id) as concert_name " +
                        "FROM selling WHERE concert_id = ? AND tickets_for_sale >= ? AND user_id != ? LIMIT 1 FOR UPDATE");
                    checkPs.setInt(1, concertId);
                    checkPs.setInt(2, ticketsToBuy);
                    checkPs.setInt(3, userId); // Hindari membeli dari diri sendiri
                    ResultSet rs = checkPs.executeQuery();
                    if (!rs.next()) {
                        throw new SQLException("No suitable ticket offer found for Concert ID " + concertId + "!");
                    }
                    availableTickets = rs.getInt("tickets_for_sale");
                    price = rs.getDouble("price_per_ticket");
                    concertName = rs.getString("concert_name");
                    System.out.println("Found selling offer: Tickets=" + availableTickets + ", Price=" + price);

                    if (availableTickets < ticketsToBuy) {
                        throw new SQLException("Not enough tickets available from seller! Available: " + availableTickets + ", Requested: " + ticketsToBuy);
                    }
                }

                // Tambahkan ke keranjang di UserDashboard
                parent.addToCart(concertId, concertName, price, ticketsToBuy);
                JOptionPane.showMessageDialog(this, ticketsToBuy + " ticket(s) for " + concertName + " added to cart!");
                dispose();
            }
        } catch (NumberFormatException ex) {
            System.out.println("NumberFormatException: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Concert ID and Tickets.");
        } catch (IllegalArgumentException ex) {
            System.out.println("IllegalArgumentException: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage());
        }
    }
}