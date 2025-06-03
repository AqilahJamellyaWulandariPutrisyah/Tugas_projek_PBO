package ui;

import db.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class MyPurchasesFrame extends JFrame {
    private JTable table = new JTable();

    public MyPurchasesFrame(int userId) {
        // Set cross-platform look and feel to match AdminDashboard and UserDashboard
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Failed to set Cross-Platform Look and Feel: " + ex.getMessage());
        }

        setTitle("My Purchases");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 350);
        setResizable(false);

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

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("My Purchases", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Buy ID", "Concert", "Tickets", "Price", "Date"}, 0);
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT b.id, c.name, b.tickets_bought, b.price_per_ticket, DATE_FORMAT(b.purchase_date, '%Y-%m-%d %H:%i:%s') AS purchase_date " +
                "FROM buying b JOIN concerts c ON b.concert_id = c.id WHERE b.buyer_id = ?")) {
            ps.setInt(1, userId);
            System.out.println("Fetching purchases for userId: " + userId);
            ResultSet rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("tickets_bought"),
                    rs.getDouble("price_per_ticket"),
                    rs.getString("purchase_date")
                });
                rowCount++;
            }
            System.out.println("Fetched " + rowCount + " purchase records for userId: " + userId);
        } catch (SQLException ex) {
            System.err.println("Error loading purchases: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading purchases: " + ex.getMessage());
        }

        table.setModel(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        table.setBackground(new Color(240, 240, 240)); // Light gray background to match AdminDashboard
        table.setForeground(new Color(50, 50, 50));
        table.setSelectionBackground(new Color(173, 216, 230)); // Light blue for selected row
        table.setSelectionForeground(new Color(50, 50, 50));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        JTableHeader header = table.getTableHeader();
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setLocationRelativeTo(null);
    }
}