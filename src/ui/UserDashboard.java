package ui;

import db.Database;
import model.Concert;
import model.Selling;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends JFrame {
    private int userId;
    private JTable concertTable = new JTable();
    private JButton buyBtn = new JButton("Buy Ticket");
    private JButton paymentBtn = new JButton("Proceed to Payment");
    private JButton sellBtn = new JButton("Sell Ticket");
    private JButton myTicketsBtn = new JButton("My Purchases");
    private JButton logoutBtn = new JButton("Logout");
    private List<CartItem> cart = new ArrayList<>(); // Keranjang sementara

    // Kelas untuk menyimpan item di keranjang
    public static class CartItem {
        int concertId;
        String concertName;
        double price;
        int quantity;

        CartItem(int concertId, String concertName, double price, int quantity) {
            this.concertId = concertId;
            this.concertName = concertName;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public UserDashboard(int userId) {
        this.userId = userId;
        setTitle("User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setResizable(false);

        // Set look and feel to match AdminDashboard
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);
        } catch (Exception ex) {
            System.err.println("Failed to set Cross-Platform Look and Feel: " + ex.getMessage());
        }

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(33, 150, 243), 0, getHeight(), new Color(200, 220, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("KONSERIA", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel with card-like appearance
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setOpaque(true);
        centerPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(15, 15, 15, 15)));
        centerPanel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 50)),
            centerPanel.getBorder()));

        // Table setup
        refreshConcerts();
        JScrollPane scrollPane = new JScrollPane(concertTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Updated Table styling to match AdminDashboard
        concertTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        concertTable.setRowHeight(30);
        concertTable.setGridColor(new Color(200, 200, 200));
        concertTable.setShowGrid(true);
        concertTable.setBackground(new Color(240, 240, 240)); // Light gray background for the table
        concertTable.setForeground(new Color(50, 50, 50));
        concertTable.setSelectionBackground(new Color(173, 216, 230)); // Light blue for selected row
        concertTable.setSelectionForeground(new Color(50, 50, 50));

        // Custom renderer for alternating row colors
        concertTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : new Color(255, 255, 255)); // Alternating row colors
                } else {
                    c.setBackground(new Color(173, 216, 230)); // Light blue for selected row
                }
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Style table header
        JTableHeader header = concertTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(33, 150, 243)); // Blue header background
        header.setForeground(Color.BLACK); // Black text for better contrast
        header.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
        header.setOpaque(true); // Ensure the header is opaque for consistent background color
        header.setReorderingAllowed(false); // Prevent column reordering
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(33, 150, 243)); // Consistent blue background
                c.setForeground(Color.BLACK); // Black text for visibility
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.WHITE);

        // Button styling
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(140, 40);

        buyBtn.setFont(buttonFont);
        buyBtn.setBackground(new Color(33, 150, 243));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setBorder(new RoundedBorder(10));
        buyBtn.setFocusPainted(false);
        buyBtn.setOpaque(true);
        buyBtn.setContentAreaFilled(true);
        buyBtn.setBorderPainted(true);
        buyBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10); // Match rounded corners
                }
                super.paint(g, c);
            }
        });
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buyBtn.setPreferredSize(buttonSize);
        buyBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buyBtn.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buyBtn.setBackground(new Color(33, 150, 243));
            }
        });

        paymentBtn.setFont(buttonFont);
        paymentBtn.setBackground(new Color(76, 175, 80));
        paymentBtn.setForeground(Color.WHITE);
        paymentBtn.setBorder(new RoundedBorder(10));
        paymentBtn.setFocusPainted(false);
        paymentBtn.setOpaque(true);
        paymentBtn.setContentAreaFilled(true);
        paymentBtn.setBorderPainted(true);
        paymentBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                }
                super.paint(g, c);
            }
        });
        paymentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        paymentBtn.setPreferredSize(buttonSize);
        paymentBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                paymentBtn.setBackground(new Color(60, 140, 64));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                paymentBtn.setBackground(new Color(76, 175, 80));
            }
        });

        sellBtn.setFont(buttonFont);
        sellBtn.setBackground(new Color(33, 150, 243));
        sellBtn.setForeground(Color.WHITE);
        sellBtn.setBorder(new RoundedBorder(10));
        sellBtn.setFocusPainted(false);
        sellBtn.setOpaque(true);
        sellBtn.setContentAreaFilled(true);
        sellBtn.setBorderPainted(true);
        sellBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                }
                super.paint(g, c);
            }
        });
        sellBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sellBtn.setPreferredSize(buttonSize);
        sellBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sellBtn.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sellBtn.setBackground(new Color(33, 150, 243));
            }
        });

        myTicketsBtn.setFont(buttonFont);
        myTicketsBtn.setBackground(new Color(33, 150, 243));
        myTicketsBtn.setForeground(Color.WHITE);
        myTicketsBtn.setBorder(new RoundedBorder(10));
        myTicketsBtn.setFocusPainted(false);
        myTicketsBtn.setOpaque(true);
        myTicketsBtn.setContentAreaFilled(true);
        myTicketsBtn.setBorderPainted(true);
        myTicketsBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                }
                super.paint(g, c);
            }
        });
        myTicketsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        myTicketsBtn.setPreferredSize(buttonSize);
        myTicketsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                myTicketsBtn.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                myTicketsBtn.setBackground(new Color(33, 150, 243));
            }
        });

        logoutBtn.setFont(buttonFont);
        logoutBtn.setBackground(new Color(255, 69, 58));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorder(new RoundedBorder(10));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setBorderPainted(true);
        logoutBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                }
                super.paint(g, c);
            }
        });
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(buttonSize);
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(200, 50, 45));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(255, 69, 58));
            }
        });

        buttonPanel.add(buyBtn);
        buttonPanel.add(paymentBtn);
        buttonPanel.add(sellBtn);
        buttonPanel.add(myTicketsBtn);
        buttonPanel.add(logoutBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action listeners
        buyBtn.addActionListener(e -> {
            BuyTicketFrame buyFrame = new BuyTicketFrame(userId, this);
            buyFrame.setVisible(true);
        });
        paymentBtn.addActionListener(e -> showCartAndProceed());
        sellBtn.addActionListener(e -> new SellTicketFrame(userId).setVisible(true));
        myTicketsBtn.addActionListener(e -> new MyPurchasesFrame(userId).setVisible(true));
        logoutBtn.addActionListener(e -> logout());

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
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        public boolean isBorderOpaque() {
            return false;
        }
    }

    // Metode untuk menambahkan item ke keranjang
    public void addToCart(int concertId, String concertName, double price, int quantity) {
        cart.add(new CartItem(concertId, concertName, price, quantity));
        JOptionPane.showMessageDialog(this, quantity + " ticket(s) for " + concertName + " added to cart!");
    }

    // Metode untuk menampilkan keranjang dan melanjutkan ke pembayaran
    private void showCartAndProceed() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add some tickets first.");
            return;
        }

        // Hitung total harga
        double total = 0;
        for (CartItem item : cart) {
            total += item.price * item.quantity;
        }

        // Buat dialog untuk menampilkan keranjang
        JDialog cartDialog = new JDialog(this, "Cart Summary", true);
        cartDialog.setSize(400, 300);
        cartDialog.setLayout(new BorderLayout());
        cartDialog.setLocationRelativeTo(this);

        JPanel cartPanel = new JPanel(new GridBagLayout());
        cartPanel.setBackground(new Color(245, 245, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextArea cartText = new JTextArea();
        cartText.setEditable(false);
        cartText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartText.setBackground(new Color(245, 245, 255));
        StringBuilder cartContent = new StringBuilder("Cart Items:\n\n");
        for (CartItem item : cart) {
            cartContent.append(item.concertName)
                       .append(" - Quantity: ")
                       .append(item.quantity)
                       .append(" - Price per ticket: ")
                       .append(String.format("%.2f", item.price))
                       .append(" - Subtotal: ")
                       .append(String.format("%.2f", item.price * item.quantity))
                       .append("\n");
        }
        cartContent.append("\nTotal: ").append(String.format("%.2f", total));
        cartText.setText(cartContent.toString());

        JScrollPane scrollPane = new JScrollPane(cartText);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        cartPanel.add(scrollPane, gbc);

        JButton payBtn = new JButton("Pay Now");
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.setBackground(new Color(76, 175, 80));
        payBtn.setForeground(Color.WHITE);
        payBtn.setBorder(new RoundedBorder(10));
        payBtn.setFocusPainted(false);
        payBtn.setOpaque(true);
        payBtn.setContentAreaFilled(true);
        payBtn.setBorderPainted(true);
        payBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                }
                super.paint(g, c);
            }
        });
        payBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                payBtn.setBackground(new Color(60, 140, 64));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                payBtn.setBackground(new Color(76, 175, 80));
            }
        });
        payBtn.addActionListener(e -> {
            new PaymentFrame(this, cart).setVisible(true);
            cartDialog.dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        cartPanel.add(payBtn, gbc);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(255, 69, 58));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setBorder(new RoundedBorder(10));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        cancelBtn.setBorderPainted(true);
        cancelBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                }
                super.paint(g, c);
            }
        });
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(200, 50, 45));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(new Color(255, 69, 58));
            }
        });
        cancelBtn.addActionListener(e -> cartDialog.dispose());
        gbc.gridx = 1;
        cartPanel.add(cancelBtn, gbc);

        cartDialog.add(cartPanel, BorderLayout.CENTER);
        cartDialog.setVisible(true);
    }

    public void refreshConcerts() {
        try {
            System.out.println("Starting refreshConcerts...");
            try (Connection con = Database.getConnection()) {
                if (con == null) {
                    throw new SQLException("Database connection is null!");
                }
                System.out.println("Database connection established.");

                // Validate table schema
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet columns = metaData.getColumns(null, null, "concerts", null);
                boolean hasId = false, hasName = false, hasDate = false, hasLocation = false, hasPrice = false, hasTicketsAvailable = false;
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    System.out.println("Found column in concerts table: " + columnName);
                    if ("id".equalsIgnoreCase(columnName)) hasId = true;
                    if ("name".equalsIgnoreCase(columnName)) hasName = true;
                    if ("date".equalsIgnoreCase(columnName)) hasDate = true;
                    if ("location".equalsIgnoreCase(columnName)) hasLocation = true;
                    if ("price".equalsIgnoreCase(columnName)) hasPrice = true;
                    if ("tickets_available".equalsIgnoreCase(columnName)) hasTicketsAvailable = true;
                }
                if (!hasId || !hasName || !hasDate || !hasLocation || !hasPrice || !hasTicketsAvailable) {
                    String missingColumns = "";
                    if (!hasId) missingColumns += "id, ";
                    if (!hasName) missingColumns += "name, ";
                    if (!hasDate) missingColumns += "date, ";
                    if (!hasLocation) missingColumns += "location, ";
                    if (!hasPrice) missingColumns += "price, ";
                    if (!hasTicketsAvailable) missingColumns += "tickets_available, ";
                    throw new SQLException("Table 'concerts' is missing required columns: " + missingColumns.substring(0, missingColumns.length() - 2));
                }
                System.out.println("Table schema validated successfully.");

                // Fetch concert data
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT * FROM concerts")) {
                    System.out.println("Query executed successfully.");

                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", "Date", "Location", "Price", "Tickets"}, 0);

                    int rowCount = 0;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("date"),
                            rs.getString("location"),
                            rs.getDouble("price"),
                            rs.getInt("tickets_available")
                        });
                        rowCount++;
                    }
                    System.out.println("Fetched " + rowCount + " rows from concerts table.");

                    if (concertTable == null) {
                        throw new IllegalStateException("concertTable is null!");
                    }
                    concertTable.setModel(model);
                    System.out.println("Table model updated successfully.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in refreshConcerts: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load concerts: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            System.out.println("IllegalStateException in refreshConcerts: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Application error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.out.println("Unexpected error in refreshConcerts: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    public int getUserId() {
        return userId;
    }

    public void clearCart() {
        cart.clear();
    }
}