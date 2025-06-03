package ui;

import db.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddEditConcertFrame extends JFrame {
    private JTextField nameField = new JTextField(15);
    private JTextField dateField = new JTextField(10);
    private JTextField locationField = new JTextField(15);
    private JTextField priceField = new JTextField(7);
    private JTextField stockField = new JTextField(5);
    private JButton saveBtn = new JButton("Save");
    private AdminDashboard parent;
    private Integer concertId; // null for add, else edit

    public AddEditConcertFrame(AdminDashboard parent, Integer concertId) {
        this.parent = parent;
        this.concertId = concertId;
        setTitle(concertId == null ? "Add Concert" : "Edit Concert");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 250);

        // Use GridBagLayout for better organization
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally

        // Row 1: Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(nameField, gbc);

        // Row 2: Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(dateField, gbc);

        // Row 3: Location
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(locationField, gbc);

        // Row 4: Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(priceField, gbc);

        // Row 5: Stock
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Stock:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        p.add(stockField, gbc);

        // Row 6: Save Button (centered)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        p.add(saveBtn, gbc);

        add(p);

        if (concertId != null) loadConcert();

        saveBtn.addActionListener(e -> saveConcert());
        setLocationRelativeTo(null);
    }

    private void loadConcert() {
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM concerts WHERE id=?")) {
            ps.setInt(1, concertId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                dateField.setText(rs.getString("date"));
                locationField.setText(rs.getString("location"));
                priceField.setText(rs.getString("price"));
                stockField.setText(rs.getString("tickets_available"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveConcert() {
    try (Connection con = Database.getConnection()) {
        PreparedStatement ps;
        if (concertId == null) {
            ps = con.prepareStatement(
                "INSERT INTO concerts (name, date, location, price, tickets_available) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, nameField.getText());
            ps.setString(2, dateField.getText());
            ps.setString(3, locationField.getText());
            ps.setDouble(4, Double.parseDouble(priceField.getText()));
            ps.setInt(5, Integer.parseInt(stockField.getText()));
        } else {
            ps = con.prepareStatement(
                "UPDATE concerts SET name=?, date=?, location=?, price=?, tickets_available=? WHERE id=?");
            ps.setString(1, nameField.getText());
            ps.setString(2, dateField.getText());
            ps.setString(3, locationField.getText());
            ps.setDouble(4, Double.parseDouble(priceField.getText()));
            ps.setInt(5, Integer.parseInt(stockField.getText()));
            ps.setInt(6, concertId);
        }
        ps.executeUpdate();
        parent.refreshConcerts();
        dispose();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid number format for price or stock.");
    }
    }
}