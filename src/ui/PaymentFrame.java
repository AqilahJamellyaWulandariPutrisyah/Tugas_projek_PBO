package ui;

import db.Database;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.List;

public class PaymentFrame extends JFrame {
    private JLabel totalPriceLabel;
    private JButton payBtn = new JButton("Pay Now");
    private List<UserDashboard.CartItem> cart;
    private int concertId = -1;
    private int userId;
    private JTextField quantityField;
    private JLabel concertNameLabel;
    private JLabel concertPriceLabel;
    private JLabel ticketsAvailableLabel;
    private JFrame parent;
    private boolean isDirectPurchase = false;

    public PaymentFrame(JFrame parent, List<UserDashboard.CartItem> cart) {
        this.parent = parent;
        this.cart = cart;
        this.userId = ((UserDashboard) parent).getUserId();
        this.isDirectPurchase = false;
        initUI();
    }

    public PaymentFrame(JFrame parent, int concertId, int userId) {
        this.parent = parent;
        this.concertId = concertId;
        this.userId = userId;
        this.isDirectPurchase = true;
        initUI();
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Failed to set Cross-Platform Look and Feel: " + ex.getMessage());
        }

        setTitle("Process Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(33, 150, 243), 0, getHeight(), new Color(245, 245, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel(isDirectPurchase ? "Direct Payment" : "Cart Summary", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        if (isDirectPurchase) {
            initDirectPurchaseUI(mainPanel);
        } else {
            initCartPurchaseUI(mainPanel);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.setBackground(new Color(76, 175, 80));
        payBtn.setForeground(Color.WHITE);
        payBtn.setBorder(new RoundedBorder(12));
        payBtn.setFocusPainted(false);
        payBtn.setOpaque(true);
        payBtn.setContentAreaFilled(true);
        payBtn.setBorderPainted(true);
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                Color bg = model.isRollover() ? new Color(60, 140, 64) : new Color(76, 175, 80);
                g.setColor(bg);
                g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g, c);
            }
        });
        payBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                payBtn.setBackground(new Color(60, 140, 64));
                payBtn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                payBtn.setBackground(new Color(76, 175, 80));
                payBtn.repaint();
            }
        });
        buttonPanel.add(payBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(244, 67, 54));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBorder(new RoundedBorder(12));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        cancelBtn.setBorderPainted(true);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                Color bg = model.isRollover() ? new Color(200, 50, 40) : new Color(244, 67, 54);
                g.setColor(bg);
                g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g, c);
            }
        });
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(200, 50, 40));
                cancelBtn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(new Color(244, 67, 54));
                cancelBtn.repaint();
            }
        });
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        payBtn.addActionListener(e -> processPayment());
        pack();
        setLocationRelativeTo(null);
    }

    private void initDirectPurchaseUI(JPanel mainPanel) {
        JPanel cartPanel = new JPanel(new GridBagLayout());
        cartPanel.setBackground(new Color(245, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);

        loadConcertDetails();

        JLabel nameLabel = new JLabel("Concert:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        cartPanel.add(nameLabel, gbc);

        concertNameLabel = new JLabel(concertId == -1 ? "Loading..." : "");
        concertNameLabel.setFont(labelFont);
        concertNameLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cartPanel.add(concertNameLabel, gbc);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(labelFont);
        priceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        cartPanel.add(priceLabel, gbc);

        concertPriceLabel = new JLabel("0.00");
        concertPriceLabel.setFont(labelFont);
        concertPriceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cartPanel.add(concertPriceLabel, gbc);

        JLabel ticketsLabel = new JLabel("Tickets Available:");
        ticketsLabel.setFont(labelFont);
        ticketsLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        cartPanel.add(ticketsLabel, gbc);

        ticketsAvailableLabel = new JLabel("Loading...");
        ticketsAvailableLabel.setFont(labelFont);
        ticketsAvailableLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cartPanel.add(ticketsAvailableLabel, gbc);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(labelFont);
        quantityLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        cartPanel.add(quantityLabel, gbc);

        quantityField = new JTextField(5);
        quantityField.setFont(fieldFont);
        quantityField.setBorder(new RoundedBorder(10));
        quantityField.setBackground(new Color(255, 255, 255));
        quantityField.setForeground(new Color(50, 50, 50));
        quantityField.setText("1");
        quantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateTotalPrice();
            }
        });
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cartPanel.add(quantityField, gbc);

        JLabel totalLabel = new JLabel("Total Price:");
        totalLabel.setFont(labelFont);
        totalLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        cartPanel.add(totalLabel, gbc);

        totalPriceLabel = new JLabel("0.00");
        totalPriceLabel.setFont(labelFont);
        totalPriceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        cartPanel.add(totalPriceLabel, gbc);

        updateTotalPrice();

        mainPanel.add(cartPanel, BorderLayout.CENTER);
    }

    private void initCartPurchaseUI(JPanel mainPanel) {
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(new Color(245, 245, 255));
        cartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (cart == null || cart.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty.", JLabel.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(50, 50, 50));
            cartPanel.add(emptyLabel, BorderLayout.CENTER);
            payBtn.setEnabled(false);
        } else {
            System.out.println("Cart size: " + cart.size());

            String[] columnNames = {"Concert Name", "Quantity", "Price per Ticket", "Subtotal"};
            Object[][] data = new Object[cart.size()][4];
            double total = 0;
            for (int i = 0; i < cart.size(); i++) {
                UserDashboard.CartItem item = cart.get(i);
                data[i][0] = item.concertName;
                data[i][1] = item.quantity;
                data[i][2] = String.format("%.2f", item.price);
                data[i][3] = String.format("%.2f", item.price * item.quantity);
                total += item.price * item.quantity;
            }

            JTable table = new JTable(data, columnNames);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.setEnabled(false);
            
            // Styling tabel agar seragam dengan UserDashboard
            table.setGridColor(new Color(200, 200, 200));
            table.setShowGrid(true);
            table.setBackground(new Color(240, 240, 240));
            table.setForeground(new Color(50, 50, 50));
            table.setSelectionBackground(new Color(173, 216, 230));
            table.setSelectionForeground(new Color(50, 50, 50));

            // Custom renderer untuk baris
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : new Color(255, 255, 255));
                    } else {
                        c.setBackground(new Color(173, 216, 230));
                    }
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    return c;
                }
            });

            // Style header tabel
            JTableHeader header = table.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 12));
            header.setBackground(new Color(33, 150, 243));
            header.setForeground(Color.BLACK);
            header.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            header.setOpaque(true);
            header.setReorderingAllowed(false);
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(new Color(33, 150, 243));
                    c.setForeground(Color.BLACK);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    return c;
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(550, 200));
            cartPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            totalPanel.setOpaque(false);
            JLabel totalLabel = new JLabel("Total Price:");
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            totalLabel.setForeground(new Color(50, 50, 50));
            totalPriceLabel = new JLabel(String.format("%.2f", total));
            totalPriceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            totalPriceLabel.setForeground(new Color(50, 50, 50));
            totalPanel.add(totalLabel);
            totalPanel.add(totalPriceLabel);
            cartPanel.add(totalPanel, BorderLayout.SOUTH);

            // Debugging: Cek jumlah baris tabel
            System.out.println("Table row count: " + table.getRowCount());
        }

        mainPanel.add(cartPanel, BorderLayout.CENTER);
    }

    private void loadConcertDetails() {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM concerts WHERE id=?")) {
            ps.setInt(1, concertId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                concertNameLabel.setText(rs.getString("name"));
                double price = rs.getDouble("price");
                concertPriceLabel.setText(String.format("%.2f", price));
                int ticketsAvailable = rs.getInt("tickets_available");
                ticketsAvailableLabel.setText(String.valueOf(ticketsAvailable));
            } else {
                JOptionPane.showMessageDialog(this, "Concert not found!");
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading concert details: " + ex.getMessage());
            dispose();
        }
    }

    private void updateTotalPrice() {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(concertPriceLabel.getText());
            double total = quantity * price;
            totalPriceLabel.setText(String.format("%.2f", total));
        } catch (NumberFormatException ex) {
            totalPriceLabel.setText("0.00");
        }
    }

    private void processPayment() {
        Connection con = null;
        try {
            con = Database.getConnection();
            if (con == null) {
                throw new SQLException("Database connection is null!");
            }
            con.setAutoCommit(false);

            if (isDirectPurchase) {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than 0.");
                }

                int availableTickets;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT tickets_available FROM concerts WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, concertId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new SQLException("Concert ID " + concertId + " not found!");
                    }
                    availableTickets = rs.getInt("tickets_available");
                    if (availableTickets < quantity) {
                        throw new SQLException("Not enough tickets available. Available: " + availableTickets + ", Requested: " + quantity);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE concerts SET tickets_available = tickets_available - ? WHERE id = ?")) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, concertId);
                    int rowsUpdated = ps.executeUpdate();
                    if (rowsUpdated == 0) {
                        throw new SQLException("Failed to update tickets for concert ID: " + concertId);
                    }
                }

                try (PreparedStatement insertPs = con.prepareStatement(
                        "INSERT INTO buying (buyer_id, seller_id, concert_id, tickets_bought, price_per_ticket, purchase_date) VALUES (?, NULL, ?, ?, ?, NOW())")) {
                    double price = Double.parseDouble(concertPriceLabel.getText());
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, concertId);
                    insertPs.setInt(3, quantity);
                    insertPs.setDouble(4, price);
                    insertPs.executeUpdate();
                }

                con.commit();
                JOptionPane.showMessageDialog(this, "Payment successful! Tickets purchased: " + quantity);
                if (parent instanceof AdminDashboard) {
                    ((AdminDashboard) parent).refreshConcerts();
                }
                dispose();
            } else {
                if (cart == null || cart.isEmpty()) {
                    throw new IllegalStateException("Cannot process payment: Cart is empty.");
                }

                for (UserDashboard.CartItem item : cart) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT tickets_available FROM concerts WHERE id = ? FOR UPDATE")) {
                        ps.setInt(1, item.concertId);
                        ResultSet rs = ps.executeQuery();
                        if (!rs.next()) {
                            throw new SQLException("Concert ID " + item.concertId + " not found!");
                        }
                        int availableTickets = rs.getInt("tickets_available");
                        if (availableTickets < item.quantity) {
                            throw new SQLException("Not enough tickets available for " + item.concertName + ". Available: " + availableTickets + ", Requested: " + item.quantity);
                        }
                    }

                    Integer sellerId = null;
                    try (PreparedStatement checkPs = con.prepareStatement(
                            "SELECT id, user_id, tickets_for_sale FROM selling WHERE concert_id = ? AND tickets_for_sale >= ? LIMIT 1 FOR UPDATE")) {
                        checkPs.setInt(1, item.concertId);
                        checkPs.setInt(2, item.quantity);
                        ResultSet rs = checkPs.executeQuery();
                        if (rs.next()) {
                            sellerId = rs.getInt("user_id");
                            int sellingId = rs.getInt("id");
                            int ticketsForSale = rs.getInt("tickets_for_sale");
                            try (PreparedStatement updateSellingPs = con.prepareStatement(
                                    "UPDATE selling SET tickets_for_sale = tickets_for_sale - ? WHERE id = ?")) {
                                updateSellingPs.setInt(1, item.quantity);
                                updateSellingPs.setInt(2, sellingId);
                                updateSellingPs.executeUpdate();
                            }
                        }
                    }

                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE concerts SET tickets_available = tickets_available - ? WHERE id = ?")) {
                        ps.setInt(1, item.quantity);
                        ps.setInt(2, item.concertId);
                        int rowsUpdated = ps.executeUpdate();
                        if (rowsUpdated == 0) {
                            throw new SQLException("Failed to update tickets for concert ID: " + item.concertId);
                        }
                    }

                    try (PreparedStatement insertPs = con.prepareStatement(
                            "INSERT INTO buying (buyer_id, seller_id, concert_id, tickets_bought, price_per_ticket, purchase_date) VALUES (?, ?, ?, ?, ?, NOW())")) {
                        insertPs.setInt(1, userId);
                        if (sellerId != null) {
                            insertPs.setInt(2, sellerId);
                        } else {
                            insertPs.setNull(2, Types.INTEGER);
                        }
                        insertPs.setInt(3, item.concertId);
                        insertPs.setInt(4, item.quantity);
                        insertPs.setDouble(5, item.price);
                        insertPs.executeUpdate();
                    }
                }

                con.commit();
                JOptionPane.showMessageDialog(this, "Payment successful! All tickets purchased.");
                if (parent instanceof UserDashboard) {
                    UserDashboard userDashboard = (UserDashboard) parent;
                    userDashboard.refreshConcerts();
                    userDashboard.clearCart();
                }
                dispose();
            }
        } catch (SQLException ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Payment failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

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
}