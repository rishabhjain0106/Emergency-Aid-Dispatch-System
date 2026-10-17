import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.table.*;
import java.io.*;
public class GUI extends JFrame {
    private Graph graph;
    private Dispatcher dispatcher;    private List<Admin> admins;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel statsPanel; // Store reference to stats panel

    // Colors
// Premium Modern Palette
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);    // Indigo 600
    private static final Color SECONDARY_COLOR = new Color(14, 165, 233);  // Sky 500
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);    // Emerald 500
    private static final Color DANGER_COLOR = new Color(239, 44, 44);      // Rose 500
    private static final Color WARNING_COLOR = new Color(245, 158, 11);    // Amber 500

    private static final Color DARK_BG = new Color(15, 23, 42);           // Slate 900
    private static final Color LIGHT_BG = new Color(248, 250, 252);        // Slate 50

    private static final Color TEXT_DARK = new Color(30, 41, 59);         // Slate 800
    private static final Color TEXT_LIGHT = new Color(255, 255, 255);
    private static final Color TEXT_MUTED_1 = new Color(71, 85, 105);      // Slate 600
    private static final Color TEXT_MUTED_2 = new Color(148, 163, 184);    // Slate 400
    private static final Color TEXT_LIGHT_FADED = new Color(255, 255, 255, 180);

    private static final Color BORDER_LIGHT = new Color(226, 232, 240);    // Slate 200
    private static final Color STAT_CARD_BG = new Color(30, 41, 59);       // Slate 800
    private static final Color BTN_NEUTRAL = new Color(100, 116, 139);     // Slate 500
    private static final Color BTN_PURPLE = new Color(139, 92, 246);       // Violet 500

    public GUI() {
        initData();
        initUI();
    }

    private void initData() {
        try {
            File graphFile = new File("graph.txt");
            if (!graphFile.exists()) {
                JOptionPane.showMessageDialog(this, "C:\\Users\\ishma\\Downloads\\daapbl\\src\\graph.txt not found!", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            graph = Graph.loadFromFile("graph.txt");
            dispatcher = new Dispatcher(graph);

            File volFile = new File("volunteers.json");
            if (volFile.exists()) {
                List<Volunteer> vols = DataStore.loadVolunteers("volunteers.json");
                for (Volunteer v : vols) dispatcher.addVolunteer(v);
            }

            File reqFile = new File("victim.json");
            if (reqFile.exists()) {
                List<AidRequest> reqs = DataStore.loadRequests("victim.json");
                for (AidRequest r : reqs) dispatcher.addRequest(r);
            }

            File adminFile = new File("admin.json");
            if (adminFile.exists()) {
                admins = DataStore.loadAdmins("admin.json");
            }
            if (admins == null || admins.isEmpty()) {
                admins = new ArrayList<>();
                admins.add(new Admin("admin", "admin123"));
                DataStore.saveAdmins("admin.json", admins);
            }

            dispatcher.repairAssignments();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        setTitle("🚑 Emergency Aid Dispatch System (EADS)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(LIGHT_BG);

        // Create all screens
        mainPanel.add(createHomeScreen(), "home");
        mainPanel.add(createVictimScreen(), "victim");
        mainPanel.add(createVolunteerScreen(), "volunteer");
        mainPanel.add(createAdminLoginScreen(), "adminLogin");
        mainPanel.add(createAdminScreen(), "admin");

        add(mainPanel);
        cardLayout.show(mainPanel, "home");

        // Start stats refresh timer (FIXED: use javax.swing.Timer)
        Timer timer = new Timer(5000, e -> {
            if (statsPanel != null) {
                updateStatsPanel(statsPanel);
            }
        });
        timer.start();

        // Add window listener to save on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }

    private void saveData() {
        try {
            DataStore.saveVolunteers("volunteers.json", dispatcher.volunteers);
            DataStore.saveRequests("victim.json", dispatcher.requests);
            DataStore.saveAdmins("admin.json", admins);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createHomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header with premium gradient
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BG, getWidth(), 0, PRIMARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1200, 150));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Emergency Aid Dispatch System", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(TEXT_LIGHT);
        header.add(title, BorderLayout.CENTER);

        JLabel subtitle = new JLabel("Optimizing Help Delivery Using Classical Algorithms", SwingConstants.LEFT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_LIGHT_FADED);
        header.add(subtitle, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);

        // Center - Cards
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Victim Card
        JPanel victimCard = createCard("👥", "Victim Portal",
                "Request emergency aid",
                "Register as a victim or check your request status",
                new Color(52, 152, 219));
        victimCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "victim");
            }
        });

        // Volunteer Card
        JPanel volunteerCard = createCard("🤝", "Volunteer Portal",
                "Join as a volunteer",
                "Register to help or manage your assignments",
                new Color(46, 204, 113));
        volunteerCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "volunteer");
            }
        });

        // Admin Card
        JPanel adminCard = createCard("👨‍💼", "Admin Portal",
                "System administration",
                "Manage dispatches, roads, and assignments",
                new Color(231, 76, 60));
        adminCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "adminLogin");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        cardsPanel.add(victimCard, gbc);
        gbc.gridx = 1;
        cardsPanel.add(volunteerCard, gbc);
        gbc.gridx = 2;
        cardsPanel.add(adminCard, gbc);

        panel.add(cardsPanel, BorderLayout.CENTER);

        // Stats Panel at bottom
        statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(String emoji, String title, String line1, String line2, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setPreferredSize(new Dimension(300, 220));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Initial shadow/border
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                new LineBorder(BORDER_LIGHT, 1, true)
        ));

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 5, 5, 5),
                        new LineBorder(color, 2, true)
                ));
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 5, 5, 5),
                        new LineBorder(BORDER_LIGHT, 1, true)
                ));
                card.repaint();
            }
        });

        JLabel emojiLabel = new JLabel(emoji, SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_DARK);

        JLabel line1Label = new JLabel(line1, SwingConstants.CENTER);
        line1Label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        line1Label.setForeground(TEXT_MUTED_1);

        JLabel line2Label = new JLabel(line2, SwingConstants.CENTER);
        line2Label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        line2Label.setForeground(TEXT_MUTED_2);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 8));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(line1Label);
        textPanel.add(line2Label);

        card.add(emojiLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        updateStatsPanel(panel);

        return panel;
    }

    private void updateStatsPanel(JPanel panel) {
        panel.removeAll();

        long waiting = dispatcher.requests.stream().filter(r -> !r.assigned && !r.completed).count();
        long active = dispatcher.getActiveAssignments().size();
        long completed = dispatcher.getCompletedRequests().size();
        long available = dispatcher.volunteers.stream().filter(Volunteer::canAccept).count();

        panel.add(createStatCard("⏳", "Waiting", String.valueOf(waiting), WARNING_COLOR));
        panel.add(createStatCard("🚚", "Active", String.valueOf(active), PRIMARY_COLOR));
        panel.add(createStatCard("✅", "Completed", String.valueOf(completed), SUCCESS_COLOR));
        panel.add(createStatCard("🫂", "Available Volunteers", String.valueOf(available), SECONDARY_COLOR));

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createStatCard(String emoji, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(STAT_CARD_BG);
        card.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        JLabel labelLabel = new JLabel(label, SwingConstants.RIGHT);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(TEXT_LIGHT_FADED);

        textPanel.add(valueLabel);
        textPanel.add(labelLabel);

        card.add(emojiLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createVictimScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        // Header
        JPanel header = createHeader("👥 Victim Portal", "Request emergency aid and track your status");
        panel.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JButton registerBtn = createStyledButton("📝 Register New Request", SUCCESS_COLOR);
        JButton loginBtn = createStyledButton("🔑 Login to Check Status", PRIMARY_COLOR);
        JButton backBtn = createStyledButton("← Back to Home", BTN_NEUTRAL);

        registerBtn.addActionListener(e -> showVictimRegistration());
        loginBtn.addActionListener(e -> showVictimLogin());
        backBtn.addActionListener(e -> {
            saveData();
            cardLayout.show(mainPanel, "home");
            if (statsPanel != null) updateStatsPanel(statsPanel);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(registerBtn, gbc);
        gbc.gridy = 1;
        content.add(loginBtn, gbc);
        gbc.gridy = 2;
        content.add(backBtn, gbc);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private void showVictimRegistration() {
        JDialog dialog = new JDialog(this, "Register New Request", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(30, 30, 30, 30));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(nameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(passField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(phoneLabel, gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(phoneField, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel locLabel = new JLabel("Location:");
        locLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(locLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> locationBox = new JComboBox<>();
        locationBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        for (int i = 0; i < graph.nodes; i++) {
            locationBox.addItem(i + " - " + graph.nodeNames[i]);
        }
        form.add(locationBox, gbc);

        // Aid Type
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel aidLabel = new JLabel("Aid Type:");
        aidLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(aidLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> aidTypeBox = new JComboBox<>(new String[]{"food", "medical", "water", "shelter"});
        aidTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(aidTypeBox, gbc);

        JButton submitBtn = createStyledButton("✓ Submit Request", SUCCESS_COLOR);
        JButton cancelBtn = createStyledButton("✗ Cancel", DANGER_COLOR);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String phone = phoneField.getText().trim();
            int loc = locationBox.getSelectedIndex();
            String aidType = (String) aidTypeBox.getSelectedItem();

            if (name.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                AidRequest r = new AidRequest(name, password, aidType, loc, phone);
                dispatcher.addRequest(r);
                saveData();
                JOptionPane.showMessageDialog(dialog, "✓ Request #" + r.id + " registered successfully!");
                dialog.dispose();
                if (statsPanel != null) updateStatsPanel(statsPanel);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showVictimLogin() {
        JDialog dialog = new JDialog(this, "Victim Login", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(30, 30, 30, 30));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field with label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(nameField, gbc);

        // Password field with label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(passField, gbc);

        JButton loginBtn = createStyledButton("🔑 Login", PRIMARY_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", BTN_NEUTRAL);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);

        loginBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            AidRequest r = AuthService.loginVictim(dispatcher.requests, name, password);
            if (r != null) {
                dialog.dispose();
                showVictimStatus(r);
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showVictimStatus(AidRequest r) {
        JDialog dialog = new JDialog(this, "Your Request Status", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color statusColor = r.completed ? SUCCESS_COLOR : (r.assigned ? PRIMARY_COLOR : WARNING_COLOR);
        String statusIcon = r.completed ? "✅" : (r.assigned ? "🚚" : "⏳");
        String statusText = r.getStatus();

        addInfoRow(panel, "Request ID:", String.valueOf(r.id), gbc, 0);
        addInfoRow(panel, "Name:", r.name, gbc, 1);
        addInfoRow(panel, "Aid Type:", r.aidType, gbc, 2);
        addInfoRow(panel, "Location:", graph.nodeNames[r.location], gbc, 3);
        addInfoRow(panel, "Waiting Time:", r.getWaitingMinutes() + " minutes", gbc, 4);
        addInfoRow(panel, "Status:", statusIcon + " " + statusText, gbc, 5, statusColor);

        if (r.assigned && !r.completed) {
            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
            if (v != null) {
                addInfoRow(panel, "Volunteer:", v.name, gbc, 6);
                addInfoRow(panel, "Volunteer Phone:", r.volunteerPhone, gbc, 7);
            }
        } else if (r.completed) {
            addInfoRow(panel, "Completion Time:", r.getCompletionTimeMinutes() + " minutes", gbc, 6);
            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
            if (v != null) {
                addInfoRow(panel, "Helped By:", v.name, gbc, 7);
            }
        }

        JButton closeBtn = createStyledButton("Close", PRIMARY_COLOR);
        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(closeBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createVolunteerScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        JPanel header = createHeader("🤝 Volunteer Portal", "Join as a volunteer or manage your assignments");
        panel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JButton registerBtn = createStyledButton("📝 Register as Volunteer", SUCCESS_COLOR);
        JButton loginBtn = createStyledButton("🔑 Login to Manage Tasks", PRIMARY_COLOR);
        JButton backBtn = createStyledButton("← Back to Home", BTN_NEUTRAL);

        registerBtn.addActionListener(e -> showVolunteerRegistration());
        loginBtn.addActionListener(e -> showVolunteerLogin());
        backBtn.addActionListener(e -> {
            saveData();
            cardLayout.show(mainPanel, "home");
            if (statsPanel != null) updateStatsPanel(statsPanel);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(registerBtn, gbc);
        gbc.gridy = 1;
        content.add(loginBtn, gbc);
        gbc.gridy = 2;
        content.add(backBtn, gbc);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private void showVolunteerRegistration() {
        JDialog dialog = new JDialog(this, "Register as Volunteer", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(30, 30, 30, 30));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(nameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(passField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(phoneLabel, gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(phoneField, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel locLabel = new JLabel("Location:");
        locLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(locLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> locationBox = new JComboBox<>();
        locationBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        for (int i = 0; i < graph.nodes; i++) {
            locationBox.addItem(i + " - " + graph.nodeNames[i]);
        }
        form.add(locationBox, gbc);

        // Capacity
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel capLabel = new JLabel("Capacity (max tasks):");
        capLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(capLabel, gbc);

        gbc.gridx = 1;
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        capacitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(capacitySpinner, gbc);

        JButton submitBtn = createStyledButton("✓ Register", SUCCESS_COLOR);
        JButton cancelBtn = createStyledButton("✗ Cancel", DANGER_COLOR);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String phone = phoneField.getText().trim();
            int loc = locationBox.getSelectedIndex();
            int capacity = (int) capacitySpinner.getValue();

            if (name.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Volunteer v = new Volunteer(name, password, phone, loc, capacity);
                dispatcher.addVolunteer(v);
                saveData();
                JOptionPane.showMessageDialog(dialog, "✓ Volunteer " + name + " registered successfully!");
                dialog.dispose();
                if (statsPanel != null) updateStatsPanel(statsPanel);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showVolunteerLogin() {
        JDialog dialog = new JDialog(this, "Volunteer Login", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(30, 30, 30, 30));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field with label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(nameField, gbc);

        // Password field with label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(passField, gbc);

        JButton loginBtn = createStyledButton("🔑 Login", PRIMARY_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", BTN_NEUTRAL);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);

        loginBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            Volunteer v = AuthService.loginVolunteer(dispatcher.volunteers, name, password);
            if (v != null) {
                dialog.dispose();
                showVolunteerMenu(v);
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showVolunteerMenu(Volunteer v) {
        JDialog dialog = new JDialog(this, "Volunteer Dashboard - " + v.name, true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(LIGHT_BG);

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Your Information"));
        infoPanel.add(createInfoLabel("📛 Name:", v.name));
        infoPanel.add(createInfoLabel("📍 Location:", graph.nodeNames[v.location]));
        infoPanel.add(createInfoLabel("⚡ Load:", v.currentLoad + " / " + v.capacity));
        infoPanel.add(createInfoLabel("📞 Phone:", v.phone));

        // Assigned requests table
        String[] columns = {"ID", "Victim", "Aid Type", "Location", "Waiting (min)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (int rid : v.assignedRequests) {
            for (AidRequest r : dispatcher.requests) {
                if (r.id == rid && !r.completed) {
                    model.addRow(new Object[]{
                            r.id, r.name, r.aidType, graph.nodeNames[r.location], r.getWaitingMinutes()
                    });
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Active Assignments"));

        JButton cancelBtn = createStyledButton("❌ Cancel Selected Task", DANGER_COLOR);
        JButton refreshBtn = createStyledButton("🔄 Refresh", PRIMARY_COLOR);
        JButton closeBtn = createStyledButton("Close", BTN_NEUTRAL);

        cancelBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int requestId = (int) model.getValueAt(selectedRow, 0);
                dispatcher.cancelByVolunteer(v.name, requestId);
                saveData();
                JOptionPane.showMessageDialog(dialog, "Task cancelled successfully!");
                dialog.dispose();
                showVolunteerMenu(v);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a task to cancel!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        refreshBtn.addActionListener(e -> {
            dialog.dispose();
            showVolunteerMenu(v);
        });

        closeBtn.addActionListener(e -> {
            saveData();
            dialog.dispose();
            if (statsPanel != null) updateStatsPanel(statsPanel);
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(cancelBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(closeBtn);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createAdminLoginScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        JPanel header = createHeader("👨‍💼 Admin Login", "Secure system administration access");
        panel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Admin Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Admin Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passField, gbc);

        JButton loginBtn = createStyledButton("🔑 Login", SUCCESS_COLOR);
        JButton backBtn = createStyledButton("← Back", BTN_NEUTRAL);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(loginBtn);
        btnPanel.add(backBtn);

        loginBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (AuthService.loginAdmin(name, password, admins)) {
                cardLayout.show(mainPanel, "admin");
                refreshAdminScreen();
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid admin credentials!\nDefault: admin / admin123", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "home"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(LIGHT_BG);
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdminScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);

        JPanel header = createHeader("👨‍💼 Admin Dashboard", "System control and monitoring");
        panel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 3, 15, 15));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(LIGHT_BG);

        JButton dispatchBtn = createStyledButton("🚚 Dispatch Volunteers", PRIMARY_COLOR);
        JButton viewMapBtn = createStyledButton("🗺️ View City Map", new Color(52, 152, 219));
        JButton viewLoadBtn = createStyledButton("⚖️ View Load Balance", BTN_PURPLE);
        JButton viewRequestsBtn = createStyledButton("📋 View All Requests", new Color(241, 196, 15));
        JButton blockRoadBtn = createStyledButton("🚧 Block Road", DANGER_COLOR);
        JButton unblockRoadBtn = createStyledButton("🟢 Unblock Road", SUCCESS_COLOR);
        JButton completeTaskBtn = createStyledButton("✅ Complete Task", new Color(46, 204, 113));
        JButton viewCompletedBtn = createStyledButton("📊 View Completed", new Color(52, 152, 219));
        JButton logoutBtn = createStyledButton("🚪 Logout", BTN_NEUTRAL);

        Font dashboardFont = new Font("Segoe UI", Font.BOLD, 16);
        for (JButton btn : new JButton[]{dispatchBtn, viewMapBtn, viewLoadBtn, viewRequestsBtn, blockRoadBtn, unblockRoadBtn, completeTaskBtn, viewCompletedBtn, logoutBtn}) {
            btn.setFont(dashboardFont);
            btn.setPreferredSize(new Dimension(240, 60)); // Also slightly larger boxes
        }

        dispatchBtn.addActionListener(e -> {
            dispatcher.dispatch();
            saveData();
            JOptionPane.showMessageDialog(panel, "Dispatch completed! Check assignments.");
            if (statsPanel != null) updateStatsPanel(statsPanel);
        });

        viewMapBtn.addActionListener(e -> showCityMap());
        viewLoadBtn.addActionListener(e -> showLoadBalance());
        viewRequestsBtn.addActionListener(e -> showAllRequests());
        blockRoadBtn.addActionListener(e -> showBlockRoad());
        unblockRoadBtn.addActionListener(e -> showUnblockRoad());
        completeTaskBtn.addActionListener(e -> showCompleteTask());
        viewCompletedBtn.addActionListener(e -> showCompletedTasks());
        logoutBtn.addActionListener(e -> {
            saveData();
            cardLayout.show(mainPanel, "home");
            if (statsPanel != null) updateStatsPanel(statsPanel);
        });

        content.add(dispatchBtn);
        content.add(viewMapBtn);
        content.add(viewLoadBtn);
        content.add(viewRequestsBtn);
        content.add(blockRoadBtn);
        content.add(unblockRoadBtn);
        content.add(completeTaskBtn);
        content.add(viewCompletedBtn);
        content.add(logoutBtn);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private void showCityMap() {
        JDialog dialog = new JDialog(this, "City Map", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        sb.append("=== CITY MAP ===\n\n");
        for (int u = 0; u < graph.nodes; u++) {
            for (int v = u + 1; v < graph.nodes; v++) {
                if (graph.baseAdj[u][v] != -1) {
                    String status = graph.isBlocked(u, v) ? " [BLOCKED]" : "";
                    sb.append(String.format("%s -- %s (dist: %d)%s\n",
                            graph.nodeNames[u], graph.nodeNames[v], graph.baseAdj[u][v], status));
                }
            }
        }
        textArea.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private void showLoadBalance() {
        JDialog dialog = new JDialog(this, "Volunteer Load Balance", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Name", "Location", "Load", "Assigned IDs", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        for (Volunteer v : dispatcher.volunteers) {
            model.addRow(new Object[]{
                    v.name,
                    graph.nodeNames[v.location],
                    v.currentLoad + "/" + v.capacity,
                    v.assignedRequests.toString(),
                    v.canAccept() ? "Yes" : "No"
            });
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    private void showAllRequests() {
        JDialog dialog = new JDialog(this, "All Requests", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"ID", "Victim", "Aid Type", "Location", "Wait(min)", "Status", "Volunteer"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        for (AidRequest r : dispatcher.requests) {
            String volName = "-";
            if (r.assigned && !r.completed) {
                Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
                volName = v != null ? v.name + " (active)" : "?";
            } else if (r.completed) {
                Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
                volName = v != null ? v.name + " (done)" : "?";
            }

            model.addRow(new Object[]{
                    r.id, r.name, r.aidType, graph.nodeNames[r.location],
                    r.getWaitingMinutes(), r.getStatus(), volName
            });
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    private void showBlockRoad() {
        JDialog dialog = new JDialog(this, "Block Road", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> node1Box = new JComboBox<>();
        JComboBox<String> node2Box = new JComboBox<>();
        for (int i = 0; i < graph.nodes; i++) {
            node1Box.addItem(i + " - " + graph.nodeNames[i]);
            node2Box.addItem(i + " - " + graph.nodeNames[i]);
        }

        addFormRow(panel, "Node A:", node1Box, gbc, 0);
        addFormRow(panel, "Node B:", node2Box, gbc, 1);

        JButton blockBtn = createStyledButton("Block Road", DANGER_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", BTN_NEUTRAL);

        blockBtn.addActionListener(e -> {
            int u = node1Box.getSelectedIndex();
            int v = node2Box.getSelectedIndex();
            List<AidRequest> affected = dispatcher.blockRoad(u, v);
            saveData();
            JOptionPane.showMessageDialog(dialog, "Road blocked! " + affected.size() + " requests affected.");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(blockBtn);
        btnPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUnblockRoad() {
        JDialog dialog = new JDialog(this, "Unblock Road", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> node1Box = new JComboBox<>();
        JComboBox<String> node2Box = new JComboBox<>();
        for (int i = 0; i < graph.nodes; i++) {
            node1Box.addItem(i + " - " + graph.nodeNames[i]);
            node2Box.addItem(i + " - " + graph.nodeNames[i]);
        }

        addFormRow(panel, "Node A:", node1Box, gbc, 0);
        addFormRow(panel, "Node B:", node2Box, gbc, 1);

        JButton unblockBtn = createStyledButton("Unblock Road", SUCCESS_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", BTN_NEUTRAL);

        unblockBtn.addActionListener(e -> {
            int u = node1Box.getSelectedIndex();
            int v = node2Box.getSelectedIndex();
            dispatcher.unblockRoad(u, v);
            saveData();
            JOptionPane.showMessageDialog(dialog, "Road unblocked!");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(unblockBtn);
        btnPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showCompleteTask() {
        JDialog dialog = new JDialog(this, "Complete Task", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        List<AidRequest> active = dispatcher.getActiveAssignments();
        if (active.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "No active assignments to complete!");
            dialog.dispose();
            return;
        }

        String[] columns = {"ID", "Victim", "Aid Type", "Volunteer"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        for (AidRequest r : active) {
            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
            model.addRow(new Object[]{r.id, r.name, r.aidType, v != null ? v.name : "Unknown"});
        }

        JButton completeBtn = createStyledButton("Complete Selected", SUCCESS_COLOR);
        completeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int requestId = (int) model.getValueAt(row, 0);
                dispatcher.completeTask(requestId);
                saveData();
                JOptionPane.showMessageDialog(dialog, "Task completed! Volunteer is now free.");
                dialog.dispose();
                if (statsPanel != null) updateStatsPanel(statsPanel);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a task!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(completeBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showCompletedTasks() {
        JDialog dialog = new JDialog(this, "Completed Tasks History", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);

        List<AidRequest> completed = dispatcher.getCompletedRequests();

        String[] columns = {"ID", "Victim", "Aid Type", "Location", "Completion Time (min)", "Volunteer"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        for (AidRequest r : completed) {
            Volunteer v = dispatcher.findVolunteerById(r.volunteerId);
            model.addRow(new Object[]{
                    r.id, r.name, r.aidType, graph.nodeNames[r.location],
                    r.getCompletionTimeMinutes(), v != null ? v.name : "Unknown"
            });
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    private void refreshAdminScreen() {
        // Simple refresh by showing admin screen again
        cardLayout.show(mainPanel, "admin");
    }

    // Helper methods for UI components
    private JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BG, getWidth(), 0, PRIMARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1200, 100));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_LIGHT);

        JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.LEFT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_LIGHT_FADED);

        header.add(titleLabel, BorderLayout.CENTER);
        header.add(subtitleLabel, BorderLayout.SOUTH);

        return header;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(220, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void addFormRow(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addInfoRow(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(valLbl, gbc);
    }

    private void addInfoRow(JPanel panel, String label, String value, GridBagConstraints gbc, int row, Color valueColor) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valLbl.setForeground(valueColor);
        panel.add(valLbl, gbc);
    }

    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl);
        panel.add(val);
        return panel;
    }

    public static void main(String[] args) {
        // Force Nimbus Look and Feel for cross-platform consistency
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Custom Nimbus Overrides
            UIManager.put("nimbusBase", DARK_BG);
            UIManager.put("nimbusBlueGrey", STAT_CARD_BG);
            UIManager.put("control", LIGHT_BG);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}
