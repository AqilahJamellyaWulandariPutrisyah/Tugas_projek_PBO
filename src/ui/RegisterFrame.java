package ui;

import db.Database;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton registerBtn = new JButton("Register");
    private JButton toggleSizeBtn = new JButton("Maximize");
    private JButton minimizeBtn = new JButton("Minimize");
    private boolean isMaximized = false;

    public RegisterFrame() {
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

        setTitle("Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400); // Increased height to accommodate buttons
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
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
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Reduced insets for better spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Create Account", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 255, 255));
        headerPanel.add(headerLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(headerPanel, gbc);

        // Form panel with card-like appearance
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setOpaque(true);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(20, 20, 20, 20))); // Reduced padding slightly
        // Add shadow effect
        formPanel.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 2, new Color(0, 0, 0, 50)),
            formPanel.getBorder()));

        // Styling for labels and fields
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);

        // Row 1: Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        usernameField.setFont(fieldFont);
        usernameField.setBorder(new RoundedBorder(8));
        usernameField.setBackground(new Color(240, 242, 245));
        usernameField.setForeground(new Color(33, 33, 33));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Row 2: Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(emailLabel, gbc);

        emailField.setFont(fieldFont);
        emailField.setBorder(new RoundedBorder(8));
        emailField.setBackground(new Color(240, 242, 245));
        emailField.setForeground(new Color(33, 33, 33));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        // Row 3: Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(new Color(33, 33, 33));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        passwordField.setFont(fieldFont);
        passwordField.setBorder(new RoundedBorder(8));
        passwordField.setBackground(new Color(240, 242, 245));
        passwordField.setForeground(new Color(33, 33, 33));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Row 4: Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(255, 255, 255));

        // Button styling
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerBtn.setBackground(new Color(33, 150, 243));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBorder(new RoundedBorder(10));
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
        registerBtn.setPreferredSize(new Dimension(110, 40)); // Slightly smaller width
        registerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerBtn.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerBtn.setBackground(new Color(33, 150, 243));
            }
        });

        toggleSizeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        toggleSizeBtn.setBackground(new Color(120, 120, 120));
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
        toggleSizeBtn.setPreferredSize(new Dimension(110, 40));
        toggleSizeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toggleSizeBtn.setBackground(new Color(100, 100, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                toggleSizeBtn.setBackground(new Color(120, 120, 120));
            }
        });

        minimizeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        minimizeBtn.setBackground(new Color(90, 90, 90));
        minimizeBtn.setForeground(Color.WHITE);
        minimizeBtn.setBorder(new RoundedBorder(10));
        minimizeBtn.setFocusPainted(false);
        minimizeBtn.setOpaque(true);
        minimizeBtn.setContentAreaFilled(true);
        minimizeBtn.setBorderPainted(true);
        minimizeBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                super.paint(g, c);
            }
        });
        minimizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minimizeBtn.setPreferredSize(new Dimension(110, 40));
        minimizeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeBtn.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizeBtn.setBackground(new Color(90, 90, 90));
            }
        });

        buttonPanel.add(registerBtn);
        buttonPanel.add(toggleSizeBtn);
        buttonPanel.add(minimizeBtn);
        formPanel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(formPanel, gbc);

        add(mainPanel);

        // Action listeners
        registerBtn.addActionListener(e -> registerUser());
        toggleSizeBtn.addActionListener(e -> toggleSize());
        minimizeBtn.addActionListener(e -> minimizeWindow());
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

    private void registerUser() {
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
            PreparedStatement checkPs = con.prepareStatement(
                "SELECT COUNT(*) FROM users WHERE username=? OR email=?");
            checkPs.setString(1, username);
            checkPs.setString(2, email);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Username or email already exists.");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, 'user')");
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password); // Use hashing in production!
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
        }
    }

    private void toggleSize() {
        if (isMaximized) {
            setSize(400, 400);
            toggleSizeBtn.setText("Maximize");
        } else {
            setSize(500, 500);
            toggleSizeBtn.setText("Minimize");
        }
        isMaximized = !isMaximized;
        setLocationRelativeTo(null);
    }

    private void minimizeWindow() {
        setState(Frame.ICONIFIED);
    }
}