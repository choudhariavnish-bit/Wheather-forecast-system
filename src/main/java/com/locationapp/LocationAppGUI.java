package com.locationapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * Ultra-Luxurious Live Native Java Desktop GUI Application
 * High-End Custom Graphic Rendering & Glassmorphic Design System.
 */
public class LocationAppGUI extends JFrame {

    private final LocationService locationService = new LocationService();
    private final AuthService authService = new AuthService();

    // Data structures for cascading selection
    private final Map<String, Map<String, List<String>>> locationsData = new LinkedHashMap<>();

    // Custom Swing Components
    private JComboBox<String> countryCombo;
    private JComboBox<String> stateCombo;
    private JComboBox<String> cityCombo;
    private JButton confirmBtn;
    private JButton gpsBtn;

    private JLabel pillLocationLabel;
    private JLabel toastIconLabel;
    private JLabel toastTextLabel;
    private JPanel toastPanel;

    public LocationAppGUI() {
        super("Location Portal");
        initData();
        setupGlobalGraphics();
        initComponents();
    }

    private void initData() {
        String json = locationService.getLocationsJSON();
        parseLocationsJSON(json);
    }

    private void setupGlobalGraphics() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Customize Look & Feel defaults for dark menus
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        UIManager.put("PopupMenu.background", new Color(15, 23, 42));
        UIManager.put("ComboBox.disabledForeground", new Color(100, 116, 139));
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 680);
        setResizable(false);
        setLocationRelativeTo(null);

        // Enable Anti-aliasing globally for frame
        getRootPane().putClientProperty("apple.awt.draggableWindowBackground", Boolean.TRUE);

        // Root Dark Ambient Panel
        JPanel rootPanel = new AmbientDarkRootPanel();
        rootPanel.setLayout(new BorderLayout());

        // 1. Header Navigation Bar
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Main Content Container (Glass Card Centered)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 28, 28, 28));

        JPanel selectorCard = createLuxuryGlassCard();
        selectorCard.setPreferredSize(new Dimension(460, 520));

        contentPanel.add(selectorCard);

        rootPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Initial Cascade Population
        populateCountries();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(22, 28, 8, 28));

        // Brand Icon Orb
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel("📍");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        brandPanel.add(logoIcon);

        // Location Badge Capsule Pill
        JPanel pillPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Capsule backdrop
                g2.setColor(new Color(15, 23, 42, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Subtle cyan border
                g2.setColor(new Color(56, 189, 248, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pillPanel.setOpaque(false);
        pillPanel.setBorder(new EmptyBorder(7, 16, 7, 16));

        JLabel pinIcon = new JLabel("🎯");
        pinIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        pillLocationLabel = new JLabel("San Francisco, United States");
        pillLocationLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pillLocationLabel.setForeground(new Color(56, 189, 248)); // #38bdf8

        pillPanel.add(pinIcon);
        pillPanel.add(pillLocationLabel);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(pillPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createLuxuryGlassCard() {
        JPanel card = new LuxuryGlassCardPanel();
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(26, 28, 26, 28));

        // Card Header Title & Subtitle
        JPanel cardHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        cardHeader.setOpaque(false);

        JLabel title = new JLabel("Select Location");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Choose your Country, State, and City");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(148, 163, 184)); // #94a3b8

        cardHeader.add(title);
        cardHeader.add(subtitle);

        card.add(cardHeader, BorderLayout.NORTH);

        // Form Container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // GPS Auto-Detect Button Row
        JPanel gpsRow = new JPanel(new BorderLayout());
        gpsRow.setOpaque(false);
        gpsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel coordsLabel = new JLabel("📍 Location Coordinates");
        coordsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        coordsLabel.setForeground(new Color(56, 189, 248));

        gpsBtn = new CustomPillButton("🧭 Auto GPS");
        gpsBtn.addActionListener(e -> autoDetectGPS());

        gpsRow.add(coordsLabel, BorderLayout.WEST);
        gpsRow.add(gpsBtn, BorderLayout.EAST);

        formPanel.add(gpsRow);
        formPanel.add(Box.createVerticalStrut(14));

        // Country Dropdown
        countryCombo = createCustomLuxuryComboBox();
        countryCombo.addActionListener(e -> onCountrySelected());
        formPanel.add(createFieldGroup("Country", countryCombo));
        formPanel.add(Box.createVerticalStrut(12));

        // State Dropdown
        stateCombo = createCustomLuxuryComboBox();
        stateCombo.addActionListener(e -> onStateSelected());
        formPanel.add(createFieldGroup("State / Region", stateCombo));
        formPanel.add(Box.createVerticalStrut(12));

        // City Dropdown
        cityCombo = createCustomLuxuryComboBox();
        cityCombo.addActionListener(e -> updatePillLocation());
        formPanel.add(createFieldGroup("City", cityCombo));
        formPanel.add(Box.createVerticalStrut(22));

        // Confirm Button
        confirmBtn = new CustomGradientButton("Confirm Location ➔");
        confirmBtn.addActionListener(e -> handleConfirmLocation());
        formPanel.add(confirmBtn);
        formPanel.add(Box.createVerticalStrut(14));

        // Toast Message Banner
        toastPanel = createToastBanner();
        formPanel.add(toastPanel);

        card.add(formPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createToastBanner() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isVisible()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setBackground(new Color(6, 78, 59, 200)); // Emerald dark default

        toastIconLabel = new JLabel("✨");
        toastIconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        toastTextLabel = new JLabel("Select location parameters to proceed.");
        toastTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        toastTextLabel.setForeground(new Color(248, 250, 252));

        panel.add(toastIconLabel);
        panel.add(toastTextLabel);

        return panel;
    }

    private JPanel createFieldGroup(String labelText, JComboBox<String> comboBox) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(148, 163, 184));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        group.add(label);
        group.add(Box.createVerticalStrut(4));
        group.add(comboBox);

        return group;
    }

    private JComboBox<String> createCustomLuxuryComboBox() {
        JComboBox<String> combo = new JComboBox<>() {
            @Override
            public void updateUI() {
                setUI(new CustomLuxuryComboBoxUI());
            }
        };
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(15, 23, 42));
        combo.setForeground(Color.WHITE);
        combo.setRenderer(new CustomLuxuryListCellRenderer());
        return combo;
    }

    // --- Cascade Selection Logic ---

    private void populateCountries() {
        countryCombo.removeAllItems();
        for (String c : locationsData.keySet()) {
            countryCombo.addItem(c);
        }

        if (locationsData.containsKey("India")) {
            countryCombo.setSelectedItem("India");
            onCountrySelected();
            stateCombo.setSelectedItem("Maharashtra");
            onStateSelected();
            cityCombo.setSelectedItem("Chh. Sambhajinagar");
            updatePillLocation();
        }
    }

    private void onCountrySelected() {
        stateCombo.removeAllItems();
        cityCombo.removeAllItems();

        String country = (String) countryCombo.getSelectedItem();
        if (country != null && locationsData.containsKey(country)) {
            Map<String, List<String>> states = locationsData.get(country);
            for (String s : states.keySet()) {
                stateCombo.addItem(s);
            }
            stateCombo.setEnabled(true);
            onStateSelected();
        } else {
            stateCombo.setEnabled(false);
            cityCombo.setEnabled(false);
        }
    }

    private void onStateSelected() {
        cityCombo.removeAllItems();

        String country = (String) countryCombo.getSelectedItem();
        String state = (String) stateCombo.getSelectedItem();

        if (country != null && state != null && locationsData.containsKey(country)) {
            Map<String, List<String>> states = locationsData.get(country);
            if (states.containsKey(state)) {
                List<String> cities = states.get(state);
                for (String c : cities) {
                    cityCombo.addItem(c);
                }
                cityCombo.setEnabled(true);
                updatePillLocation();
            }
        } else {
            cityCombo.setEnabled(false);
        }
    }

    private void updatePillLocation() {
        String country = (String) countryCombo.getSelectedItem();
        String state = (String) stateCombo.getSelectedItem();
        String city = (String) cityCombo.getSelectedItem();

        if (city != null && country != null) {
            pillLocationLabel.setText(city + ", " + (state != null ? state + ", " : "") + country);
        }
    }

    private void autoDetectGPS() {
        gpsBtn.setText("⏳ Detecting...");
        gpsBtn.setEnabled(false);

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            gpsBtn.setText("🧭 Auto GPS");
            gpsBtn.setEnabled(true);

            toastPanel.setBackground(new Color(6, 78, 59, 220)); // Emerald
            toastIconLabel.setText("✅");
            toastTextLabel.setText("GPS Locked: San Francisco, US");
            toastTextLabel.setForeground(new Color(74, 222, 128));

            if (locationsData.containsKey("United States")) {
                countryCombo.setSelectedItem("United States");
                onCountrySelected();
                stateCombo.setSelectedItem("California");
                onStateSelected();
                cityCombo.setSelectedItem("San Francisco");
                updatePillLocation();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void handleConfirmLocation() {
        String country = (String) countryCombo.getSelectedItem();
        String state = (String) stateCombo.getSelectedItem();
        String city = (String) cityCombo.getSelectedItem();

        String responseJson = authService.authenticate("user", "password", country, state, city);

        if (responseJson.contains("\"success\": true")) {
            toastPanel.setBackground(new Color(6, 78, 59, 220)); // Emerald success
            toastIconLabel.setText("✅");
            toastTextLabel.setText("Location confirmed for " + city + ", " + country + "!");
            toastTextLabel.setForeground(new Color(74, 222, 128));
        } else {
            toastPanel.setBackground(new Color(136, 19, 55, 220)); // Rose error
            toastIconLabel.setText("❌");
            toastTextLabel.setText(responseJson);
            toastTextLabel.setForeground(new Color(248, 113, 113));
        }
    }

    // --- JSON Parser for Location Dataset ---

    private void parseLocationsJSON(String json) {
        try {
            String trimmed = json.trim();
            if (trimmed.startsWith("{")) trimmed = trimmed.substring(1);
            if (trimmed.endsWith("}")) trimmed = trimmed.substring(0, trimmed.length() - 1);

            String[] countryBlocks = trimmed.split("\"\\s*:\\s*\\{");
            for (int i = 0; i < countryBlocks.length - 1; i++) {
                String countryPart = countryBlocks[i];
                int lastQuote = countryPart.lastIndexOf('"');
                int prevQuote = countryPart.lastIndexOf('"', lastQuote - 1);
                String countryName = countryPart.substring(prevQuote + 1, lastQuote);

                String blockContent = countryBlocks[i + 1];
                int endBrace = blockContent.indexOf('}');
                if (endBrace != -1) {
                    blockContent = blockContent.substring(0, endBrace);
                }

                Map<String, List<String>> stateMap = new LinkedHashMap<>();
                String[] stateLines = blockContent.split("\\]");

                for (String line : stateLines) {
                    if (!line.contains(":")) continue;
                    String[] parts = line.split(":");
                    String stateName = parts[0].replaceAll("[^a-zA-Z\\s\\-Á-ÿ']", "").trim();
                    if (stateName.isEmpty()) continue;

                    String citiesRaw = parts[1].replaceAll("[\\[\\]\"]", "").trim();
                    List<String> cityList = new ArrayList<>();
                    for (String c : citiesRaw.split(",")) {
                        if (!c.trim().isEmpty()) cityList.add(c.trim());
                    }
                    stateMap.put(stateName, cityList);
                }
                locationsData.put(countryName, stateMap);
            }
        } catch (Exception e) {
            initFallbackData();
        }

        if (locationsData.isEmpty()) {
            initFallbackData();
        }
    }

    private void initFallbackData() {
        Map<String, List<String>> india = new LinkedHashMap<>();
        india.put("Maharashtra", Arrays.asList("Chh. Sambhajinagar", "Mumbai", "Pune", "Nagpur", "Nashik", "Thane"));
        india.put("Delhi", Arrays.asList("New Delhi", "North Delhi", "South Delhi"));
        india.put("Karnataka", Arrays.asList("Bengaluru", "Mysuru"));
        locationsData.put("India", india);

        Map<String, List<String>> us = new LinkedHashMap<>();
        us.put("California", Arrays.asList("Los Angeles", "San Francisco", "San Diego"));
        us.put("New York", Arrays.asList("New York City", "Buffalo"));
        locationsData.put("United States", us);
    }

    // --- Custom Advanced Graphic Component Classes ---

    private static class AmbientDarkRootPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();

            // Multi-stop Deep Gradient Background
            GradientPaint gp = new GradientPaint(0, 0, new Color(3, 7, 18), w, h, new Color(15, 23, 42));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // Ambient Soft Glow Orbs
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.setColor(new Color(56, 189, 248)); // Cyan glow top right
            g2.fillOval(w - 220, -60, 320, 320);

            g2.setColor(new Color(99, 102, 241)); // Indigo glow bottom left
            g2.fillOval(-100, h - 220, 320, 320);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class LuxuryGlassCardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Translucent Glass Fill
            g2.setColor(new Color(15, 23, 42, 220));
            g2.fillRoundRect(0, 0, w, h, 28, 28);

            // Double Gradient Glowing Border
            GradientPaint borderGradient = new GradientPaint(0, 0, new Color(56, 189, 248, 140), w, h, new Color(99, 102, 241, 100));
            g2.setPaint(borderGradient);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 28, 28);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CustomGradientButton extends JButton {
        public CustomGradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            boolean hover = getModel().isRollover();
            Color color1 = hover ? new Color(2, 132, 199) : new Color(3, 105, 161);
            Color color2 = hover ? new Color(16, 185, 129) : new Color(56, 189, 248);

            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            // Subtle inner glow border
            g2.setColor(new Color(255, 255, 255, hover ? 60 : 30));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CustomPillButton extends JButton {
        public CustomPillButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(new Color(56, 189, 248));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(5, 12, 5, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            boolean hover = getModel().isRollover();

            g2.setColor(hover ? new Color(30, 41, 59, 240) : new Color(15, 23, 42, 200));
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            g2.setColor(hover ? new Color(56, 189, 248, 200) : new Color(56, 189, 248, 100));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CustomLuxuryComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton("▼");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btn.setForeground(new Color(148, 163, 184));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            return btn;
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            popup.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
            return popup;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(15, 23, 42));
            g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);

            g2.setColor(hasFocus ? new Color(56, 189, 248) : new Color(51, 65, 85));
            g2.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 12, 12);

            g2.dispose();
        }
    }

    private static class CustomLuxuryListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(2, 132, 199) : new Color(15, 23, 42));
            label.setForeground(isSelected ? Color.WHITE : new Color(248, 250, 252));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setBorder(new EmptyBorder(8, 12, 8, 12));
            return label;
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            LocationAppGUI gui = new LocationAppGUI();
            gui.pack();
            gui.setSize(540, 680);
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
            gui.toFront();
            gui.requestFocus();
            gui.setAlwaysOnTop(true);
            javax.swing.Timer timer = new javax.swing.Timer(3000, e -> gui.setAlwaysOnTop(false));
            timer.setRepeats(false);
            timer.start();
        });
    }
}
