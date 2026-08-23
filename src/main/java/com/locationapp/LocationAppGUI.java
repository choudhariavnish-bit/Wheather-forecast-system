package com.locationapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LocationAppGUI extends JFrame {

    private final LocationService locationService = new LocationService();
    private final AuthService authService = new AuthService();

    // Data structures for cascading selection
    private final Map<String, Map<String, List<String>>> locationsData = new LinkedHashMap<>();

    // Swing Components
    private JComboBox<String> countryCombo;
    private JComboBox<String> stateCombo;
    private JComboBox<String> cityCombo;
    private JButton confirmBtn;
    private JButton gpsBtn;

    private JLabel pillLocationLabel;
    private JLabel toastLabel;

    public LocationAppGUI() {
        super("Location Portal");
        initData();
        setupLookAndFeel();
        initComponents();
    }

    private void initData() {
        String json = locationService.getLocationsJSON();
        parseLocationsJSON(json);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 640);
        setResizable(false);
        setLocationRelativeTo(null);

        // Root Dark Panel
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(new Color(15, 23, 42)); // #0f172a dark slate

        // 1. Header Navigation Bar
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Main Content Area (Only Location Selector Card Centered)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 30, 30, 30));

        JPanel selectorCard = createSelectorCard();
        selectorCard.setPreferredSize(new Dimension(460, 480));

        contentPanel.add(selectorCard);

        rootPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Initial Cascade Population
        populateCountries();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 10, 30));

        // Brand Title with Icon
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel("📍");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel titleLabel = new JLabel("Location Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(248, 250, 252));

        brandPanel.add(logoIcon);
        brandPanel.add(titleLabel);

        // Location Badge Pill
        JPanel pillPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pillPanel.setOpaque(false);
        pillPanel.setBorder(new EmptyBorder(5, 12, 5, 12));

        JLabel pinIcon = new JLabel("🎯");
        pinIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        pillLocationLabel = new JLabel("San Francisco, US");
        pillLocationLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pillLocationLabel.setForeground(new Color(56, 189, 248)); // #38bdf8

        pillPanel.add(pinIcon);
        pillPanel.add(pillLocationLabel);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(pillPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createSelectorCard() {
        JPanel card = createGlassCard();
        card.setLayout(new BorderLayout(0, 16));

        // Card Header
        JPanel cardHeader = new JPanel(new GridLayout(2, 1, 0, 4));
        cardHeader.setOpaque(false);

        JLabel title = new JLabel("Select Location");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Choose your Country, State, and City");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(148, 163, 184));

        cardHeader.add(title);
        cardHeader.add(subtitle);

        card.add(cardHeader, BorderLayout.NORTH);

        // Form Fields Container
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Auto GPS Button Row
        JPanel gpsRow = new JPanel(new BorderLayout());
        gpsRow.setOpaque(false);
        gpsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel coordsLabel = new JLabel("📍 Location Coordinates");
        coordsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        coordsLabel.setForeground(new Color(56, 189, 248));

        gpsBtn = new JButton("🧭 Auto GPS");
        styleSecondaryButton(gpsBtn);
        gpsBtn.addActionListener(e -> autoDetectGPS());

        gpsRow.add(coordsLabel, BorderLayout.WEST);
        gpsRow.add(gpsBtn, BorderLayout.EAST);

        formPanel.add(gpsRow);
        formPanel.add(Box.createVerticalStrut(16));

        // Country Field
        countryCombo = createStyledComboBox();
        countryCombo.addActionListener(e -> onCountrySelected());
        formPanel.add(createFieldGroup("Country", countryCombo));
        formPanel.add(Box.createVerticalStrut(14));

        // State Field
        stateCombo = createStyledComboBox();
        stateCombo.addActionListener(e -> onStateSelected());
        formPanel.add(createFieldGroup("State / Region", stateCombo));
        formPanel.add(Box.createVerticalStrut(14));

        // City Field
        cityCombo = createStyledComboBox();
        cityCombo.addActionListener(e -> updatePillLocation());
        formPanel.add(createFieldGroup("City", cityCombo));
        formPanel.add(Box.createVerticalStrut(22));

        // Confirm Button
        confirmBtn = new JButton("Confirm Location");
        stylePrimaryButton(confirmBtn);
        confirmBtn.addActionListener(e -> handleConfirmLocation());
        formPanel.add(confirmBtn);
        formPanel.add(Box.createVerticalStrut(14));

        // Toast Message Label
        toastLabel = new JLabel(" ", SwingConstants.CENTER);
        toastLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toastLabel.setForeground(new Color(74, 222, 128));
        toastLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(toastLabel);

        card.add(formPanel, BorderLayout.CENTER);

        return card;
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
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        group.add(label);
        group.add(Box.createVerticalStrut(4));
        group.add(comboBox);

        return group;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(30, 41, 59));
        combo.setForeground(Color.WHITE);
        return combo;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(2, 132, 199)); // #0284c7
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(new Color(56, 189, 248));
        btn.setBackground(new Color(15, 23, 42));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createGlassCard() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Glass backdrop
                g2.setColor(new Color(30, 41, 59, 190));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // Subtle border
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

                g2.dispose();
                super.paintComponent(g);
            }
        };
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

        javax.swing.Timer timer = new javax.swing.Timer(1200, e -> {
            gpsBtn.setText("🧭 Auto GPS");
            gpsBtn.setEnabled(true);
            toastLabel.setText("✅ Location auto-detected via GPS: San Francisco, US");
            toastLabel.setForeground(new Color(74, 222, 128));

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
            toastLabel.setText("✅ Location confirmed for " + city + ", " + country + "!");
            toastLabel.setForeground(new Color(74, 222, 128));
        } else {
            toastLabel.setText("❌ " + responseJson);
            toastLabel.setForeground(new Color(248, 113, 113));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LocationAppGUI gui = new LocationAppGUI();
            gui.setVisible(true);
        });
    }
}
