package ui;

import db.Database;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class SellTicketFrame extends JFrame {
    private JTextField concertIdField = new JTextField(5);
    private JTextField ticketCountField = new JTextField(5);
    private JTextField priceField = new JTextField(7);
    private JButton sellBtn = new JButton("List Ticket");
    private JButton toggleSizeBtn = new JButton("Maximize");
    private int userId;
    private boolean isMaximized = false;

    public SellTicketFrame(int userId) {
        this.userId = userId;

        // Set Windows look and feel
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Failed to set Windows look and feel: " + ex.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception e) {
                System.out.println("Failed to set system look and feel: " + e.getMessage());
            }
        }

        setTitle("Sell Ticket");
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(76, 175, 80), 0, getHeight(), new Color(245, 255, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Sell Ticket", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(headerLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(headerPanel, gbc);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(150, 150, 150), 1),
            new EmptyBorder(15, 15, 15, 15)));

        // Styling for labels and fields
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Row 1: Concert ID
        JLabel concertIdLabel = new JLabel("Concert ID:");
        concertIdLabel.setFont(labelFont);
        concertIdLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(concertIdLabel, gbc);

        concertIdField.setFont(fieldFont);
        concertIdField.setBorder(new RoundedBorder(10));
        concertIdField.setBackground(new Color(255, 255, 255));
        concertIdField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(concertIdField, gbc);

        // Row 2: Tickets
        JLabel ticketCountLabel = new JLabel("Tickets:");
        ticketCountLabel.setFont(labelFont);
        ticketCountLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(ticketCountLabel, gbc);

        ticketCountField.setFont(fieldFont);
        ticketCountField.setBorder(new RoundedBorder(10));
        ticketCountField.setBackground(new Color(255, 255, 255));
        ticketCountField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(ticketCountField, gbc);

        // Row 3: Price per Ticket
        JLabel priceLabel = new JLabel("Price per Ticket:");
        priceLabel.setFont(labelFont);
        priceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(priceLabel, gbc);

        priceField.setFont(fieldFont);
        priceField.setBorder(new RoundedBorder(10));
        priceField.setBackground(new Color(255, 255, 255));
        priceField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(priceField, gbc);

        // Row 4: Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        sellBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sellBtn.setBackground(new Color(76, 175, 80));
        sellBtn.setForeground(Color.WHITE);
        sellBtn.setBorder(new RoundedBorder(12));
        sellBtn.setFocusPainted(false);
        sellBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sellBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sellBtn.setBackground(new Color(60, 140, 64));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sellBtn.setBackground(new Color(76, 175, 80));
            }
        });

        toggleSizeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toggleSizeBtn.setBackground(new Color(100, 100, 100));
        toggleSizeBtn.setForeground(Color.WHITE);
        toggleSizeBtn.setBorder(new RoundedBorder(10));
        toggleSizeBtn.setFocusPainted(false);
        toggleSizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleSizeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleSizeBtn.setBackground(new Color(80, 80, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                toggleSizeBtn.setBackground(new Color(100, 100, 100));
            }
        });

        buttonPanel.add(sellBtn);
        buttonPanel.add(toggleSizeBtn);
        formPanel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(formPanel, gbc);

        add(mainPanel);

        sellBtn.addActionListener(e -> listTicket());
        toggleSizeBtn.addActionListener(e -> toggleSize());
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

    private void listTicket() {
        try {
            String concertIdText = concertIdField.getText().trim();
            String ticketCountText = ticketCountField.getText().trim();
            String priceText = priceField.getText().trim();

            if (concertIdText.isEmpty() || ticketCountText.isEmpty() || priceText.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled.");
            }

            int concertId = Integer.parseInt(concertIdText);
            int tickets = Integer.parseInt(ticketCountText);
            double price = Double.parseDouble(priceText);

            if (tickets <= 0 || price <= 0) {
                throw new IllegalArgumentException("Tickets and price must be greater than zero.");
            }

            try (Connection con = Database.getConnection()) {
                if (con == null) {
                    throw new SQLException("Database connection is null!");
                }
                con.setAutoCommit(false);
                try {
                    PreparedStatement checkPs = con.prepareStatement(
                        "SELECT tickets_available FROM concerts WHERE id = ?");
                    checkPs.setInt(1, concertId);
                    ResultSet rs = checkPs.executeQuery();
                    if (!rs.next() || rs.getInt("tickets_available") < tickets) {
                        throw new SQLException("Not enough tickets available in concert!");
                    }

                    PreparedStatement updatePs = con.prepareStatement(
                        "UPDATE concerts SET tickets_available = tickets_available - ? WHERE id = ?");
                    updatePs.setInt(1, tickets);
                    updatePs.setInt(2, concertId);
                    updatePs.executeUpdate();

                    PreparedStatement insertPs = con.prepareStatement(
                        "INSERT INTO selling (user_id, concert_id, tickets_for_sale, price_per_ticket) VALUES (?, ?, ?, ?)");
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, concertId);
                    insertPs.setInt(3, tickets);
                    insertPs.setDouble(4, price);
                    insertPs.executeUpdate();

                    con.commit();
                    JOptionPane.showMessageDialog(this, "Ticket listed for sale!");
                    dispose();
                } catch (SQLException ex) {
                    con.rollback();
                    throw ex;
                } finally {
                    con.setAutoCommit(true);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format for concert ID, tickets, or price.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
        }
    }

    private void toggleSize() {
        if (isMaximized) {
            setSize(350, 300);
            toggleSizeBtn.setText("Maximize");
        } else {
            setSize(450, 380);
            toggleSizeBtn.setText("Minimize");
        }
        isMaximized = !isMaximized;
        setLocationRelativeTo(null);
    }
}