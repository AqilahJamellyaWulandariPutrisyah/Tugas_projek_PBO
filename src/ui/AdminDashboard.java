package ui;

import db.Database;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private JTable concertTable = new JTable();
    private JButton addBtn = new JButton("Add Concert");
    private JButton editBtn = new JButton("Edit Selected");
    private JButton deleteBtn = new JButton("Delete Selected");
    private JButton logoutBtn = new JButton("Logout");

    public AdminDashboard(int adminId) {
        // Set look and feel before creating any components
        try {
            // Use cross-platform look and feel to avoid Windows-specific overrides
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", null); // Prevent L&F from overriding background
            UIManager.put("Button.foreground", null); // Prevent L&F from overriding foreground
        } catch (Exception ex) {
            System.err.println("Failed to set Cross-Platform Look and Feel: " + ex.getMessage());
        }

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 450);
        setResizable(false);

        // Main panel with gradient background
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        refreshConcerts();

        JScrollPane scrollPane = new JScrollPane(concertTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Updated Table styling
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
        header.setReorderingAllowed(false); // Optional: Prevent column reordering
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

        // Button panel with GridBagLayout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Style and add buttons
        styleButton(addBtn, new Color(76, 175, 80), new Color(60, 140, 64));
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(addBtn, gbc);

        styleButton(editBtn, new Color(33, 150, 243), new Color(25, 118, 210));
        gbc.gridx = 1;
        buttonPanel.add(editBtn, gbc);

        styleButton(deleteBtn, new Color(244, 67, 54), new Color(211, 47, 47));
        gbc.gridx = 2;
        buttonPanel.add(deleteBtn, gbc);

        styleButton(logoutBtn, new Color(100, 100, 100), new Color(80, 80, 80));
        gbc.gridx = 3;
        buttonPanel.add(logoutBtn, gbc);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addBtn.addActionListener(e -> new AddEditConcertFrame(this, null).setVisible(true));
        editBtn.addActionListener(e -> {
            int row = concertTable.getSelectedRow();
            if (row >= 0) {
                int concertId = (int) concertTable.getValueAt(row, 0);
                new AddEditConcertFrame(this, concertId).setVisible(true);
            }
        });
        deleteBtn.addActionListener(e -> {
            int row = concertTable.getSelectedRow();
            if (row >= 0) {
                int concertId = (int) concertTable.getValueAt(row, 0);
                try (Connection con = Database.getConnection()) {
                    PreparedStatement ps = con.prepareStatement("DELETE FROM concerts WHERE id=?");
                    ps.setInt(1, concertId);
                    ps.executeUpdate();
                    refreshConcerts();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        logoutBtn.addActionListener(e -> logout());

        setLocationRelativeTo(null);
    }

    private void styleButton(JButton button, Color baseColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Override the button's UI to ensure custom painting
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                Color bg = model.isRollover() ? hoverColor : baseColor;
                g.setColor(bg);
                g.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g, c);
            }
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.repaint();
            }
        });
    }

    public void refreshConcerts() {
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM concerts")) {
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Date", "Location", "Price", "Tickets"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("date"),
                    rs.getString("location"),
                    rs.getDouble("price"),
                    rs.getInt("tickets_available")
                });
            }
            concertTable.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
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
}