package ui;

import db.Database;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton loginBtn = new JButton("Login");
    private JButton registerBtn = new JButton("Register");
    private JButton toggleSizeBtn = new JButton("Maximize");
    private boolean isMaximized = false;

    public LoginFrame() {
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

        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500); // Further increased size to ensure all components fit
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20)) {
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
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setOpaque(true);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(150, 150, 150), 1, true),
            new EmptyBorder(20, 20, 20, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Login", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.BLACK); // Changed to black for better contrast
        headerPanel.add(headerLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(headerPanel, gbc);

        // Styling for labels and fields
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Row 1: Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        usernameField.setFont(fieldFont);
        usernameField.setBorder(new RoundedBorder(10));
        usernameField.setBackground(new Color(245, 245, 245));
        usernameField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Row 2: Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(emailLabel, gbc);

        emailField.setFont(fieldFont);
        emailField.setBorder(new RoundedBorder(10));
        emailField.setBackground(new Color(245, 245, 245));
        emailField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        // Row 3: Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        passwordField.setFont(fieldFont);
        passwordField.setBorder(new RoundedBorder(10));
        passwordField.setBackground(new Color(245, 245, 245));
        passwordField.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Row 4: Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(255, 255, 255));

        // Button styling
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(110, 40);

        loginBtn.setFont(buttonFont);
        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBorder(new RoundedBorder(12));
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(true);
        loginBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                super.paint(g, c);
            }
        });
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(buttonSize);
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(33, 150, 243));
            }
        });

        registerBtn.setFont(buttonFont);
        registerBtn.setBackground(new Color(76, 175, 80));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBorder(new RoundedBorder(12));
        registerBtn.setFocusPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setContentAreaFilled(true);
        registerBtn.setBorderPainted(true);
        registerBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                super.paint(g, c);
            }
        });
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setPreferredSize(buttonSize);
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerBtn.setBackground(new Color(60, 140, 64));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerBtn.setBackground(new Color(76, 175, 80));
            }
        });

        toggleSizeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toggleSizeBtn.setBackground(new Color(100, 100, 100));
        toggleSizeBtn.setForeground(Color.WHITE);
        toggleSizeBtn.setBorder(new RoundedBorder(10));
        toggleSizeBtn.setFocusPainted(false);
        toggleSizeBtn.setOpaque(true);
        toggleSizeBtn.setContentAreaFilled(true);
        toggleSizeBtn.setBorderPainted(true);
        toggleSizeBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                super.paint(g, c);
            }
        });
        toggleSizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleSizeBtn.setPreferredSize(buttonSize);
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

        // Add buttons with debug logging
        System.out.println("Adding Login button to panel: " + loginBtn.isVisible());
        buttonPanel.add(loginBtn);
        System.out.println("Adding Register button to panel: " + registerBtn.isVisible());
        buttonPanel.add(registerBtn);
        System.out.println("Adding ToggleSize button to panel: " + toggleSizeBtn.isVisible());
        buttonPanel.add(toggleSizeBtn);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel);
        add(mainPanel);

        // Action listeners
        loginBtn.addActionListener(e -> loginUser());
        registerBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
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

    private void loginUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection con = Database.getConnection()) {
            if (con == null) {
                throw new SQLException("Database connection is null!");
            }
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND email=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password); // Compare hash in production!
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");
                dispose();
                if ("admin".equals(role)) {
                    new AdminDashboard(userId).setVisible(true);
                } else {
                    new UserDashboard(userId).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
        }
    }

    private void toggleSize() {
        if (isMaximized) {
            setSize(400, 400);
            toggleSizeBtn.setText("Maximize");
        } else {
            setSize(600, 500);
            toggleSizeBtn.setText("Minimize");
        }
        isMaximized = !isMaximized;
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}