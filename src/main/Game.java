package main;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color DARK_BG_COLOR = new Color(30, 30, 30);
    private static final Color DARK_PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color DARK_SECONDARY_COLOR = new Color(35, 147, 237);
    private static final Color DARK_ACCENT_COLOR = new Color(255, 159, 64);
    
    private JTextField inputField;
    private JButton submitButton;
    private JLabel messageLabel;
    private JLabel imageLabel;
    private JLabel analysisLabel;
    private JPanel visualizationPanel;
    private Buttons mainMenu;
    private Timer analysisTimer;
    private int animationStep = 0;
    private boolean darkMode;
    
    private SoundManager sound;
    
    public Game(Buttons mainMenu, boolean darkMode) {
        this.mainMenu = mainMenu;
        this.darkMode = darkMode;
        this.sound = new SoundManager();
        initializeUI();
        setupListeners();
//        sound.playGameBackgroundMusic();

    }

    private void initializeUI() {
        setTitle("Language Analysis Visualization");
        setSize(1000, 700);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(darkMode ? DARK_BG_COLOR : BG_COLOR);
        // Main panels
        JPanel topPanel = InputPanel();
        JPanel centerPanel = VisualizationPanel();
        JPanel bottomPanel = AnalysisPanel();

        // Panels frame with padding
        add(PaddedPanel(topPanel), BorderLayout.NORTH);
        add(PaddedPanel(centerPanel), BorderLayout.CENTER);
        add(PaddedPanel(bottomPanel), BorderLayout.SOUTH);

        // Window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (mainMenu != null) {
//                	sound.stopBackgroundMusic();
                    mainMenu.reOpenMenu();
                }
            }
        });
        // Title panel with close button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JPanel titleWrapper = new JPanel();
        titleWrapper.setOpaque(false);
        JLabel titleLabel = new JLabel("Language Analysis Game");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 35));
        titleLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
        titleWrapper.add(titleLabel);
        
        JButton closeButton = StyledButton("✕");
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.addActionListener(e -> System.exit(0));
        
        JPanel closeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closeWrapper.setOpaque(false);
        closeWrapper.add(closeButton);
        
        titlePanel.add(titleWrapper, BorderLayout.CENTER);
        titlePanel.add(closeWrapper, BorderLayout.EAST);
    }

    private JPanel PaddedPanel(JPanel panel) {
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBackground(darkMode ? DARK_BG_COLOR : BG_COLOR);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        paddedPanel.add(panel, BorderLayout.CENTER);
        return paddedPanel;
    }

    private JPanel InputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(darkMode ? DARK_BG_COLOR : BG_COLOR);

        // Input field with modern styling
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0, 40));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JButton closeButton = StyledButton("✕");
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.addActionListener(e -> {
            dispose();
            if (mainMenu != null) {
                mainMenu.reOpenMenu();
            }
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(darkMode ? DARK_BG_COLOR : BG_COLOR);
        topPanel.add(closeButton);
        panel.add(topPanel, BorderLayout.NORTH);
        // Submit button with modern styling
        submitButton = StyledButton("Analyze");

        // Components to panel
        JPanel inputWrapper = new JPanel(new BorderLayout(10, 0));
        inputWrapper.setBackground(darkMode ? DARK_BG_COLOR : BG_COLOR);
        inputWrapper.add(inputField, BorderLayout.CENTER);
        inputWrapper.add(submitButton, BorderLayout.EAST);
        
        // Instructions label
        JLabel instructionsLabel = new JLabel("Enter a sentence or word to analyze");
        instructionsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        instructionsLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        
        panel.add(instructionsLabel, BorderLayout.NORTH);
        panel.add(inputWrapper, BorderLayout.CENTER);
        
        return panel;
    }

    private JButton StyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(darkMode ? DARK_PRIMARY_COLOR.darker() : PRIMARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(darkMode ? DARK_SECONDARY_COLOR : SECONDARY_COLOR);
                } else {
                    g2d.setColor(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(darkMode ? Color.WHITE : Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }

    private JPanel VisualizationPanel() {
        visualizationPanel = new JPanel(new BorderLayout(10, 10));
        visualizationPanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
        visualizationPanel.setBorder(BorderFactory.createLineBorder(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR, 2, true));

        // Message label for status updates
        messageLabel = new JLabel("Enter a word or sentence to begin analysis", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Image display area with scroll pane
        imageLabel = new JLabel(new ImageIcon(), SwingConstants.CENTER);
        imageLabel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
        imageLabel.setOpaque(true);
        
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);

        visualizationPanel.add(messageLabel, BorderLayout.NORTH);
        visualizationPanel.add(scrollPane, BorderLayout.CENTER);
        
        return visualizationPanel;
    }

    private JPanel AnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(darkMode ? DARK_BG_COLOR : BG_COLOR);
        
        analysisLabel = new JLabel();
        analysisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        analysisLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add a border to separate analysis from visualization
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR));
        panel.add(analysisLabel, BorderLayout.CENTER);
        
        return panel;
    }

    private void setupListeners() {
        submitButton.addActionListener(e -> processInput());
        
        inputField.addActionListener(e -> processInput());
        
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processInput();
                }
            }
        });

        analysisTimer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationStep++;
                updateAnalysisAnimation();
            }
        });
    }

    // Animation for analyzing
    private void updateAnalysisAnimation() {
        switch (animationStep) {
            case 1:
                updateMessage("Analyzing.", WARNING_COLOR);
                break;
            case 2:
                updateMessage("Analyzing..", WARNING_COLOR);
                break;
            case 3:
                updateMessage("Analyzing...", WARNING_COLOR);
                break;
            case 4:
                analysisTimer.stop();
                animationStep = 0;
                performAnalysis(inputField.getText().trim());
                break;
        }
    }

    void processInput() {
        String input = inputField.getText().trim();
        
        if (input.isEmpty()) {
            showError("Please enter a word or sentence to analyze.");
            return;
        }
        // Reset UI state
        clearAnalysis();
        // Start analysis animation
        animationStep = 0;
        analysisTimer.start();
        updateMessage("Initiating analysis...", WARNING_COLOR);
    }

    private void clearAnalysis() {
        imageLabel.setIcon(null);
        analysisLabel.setText("");
    }
    

 // Update the performAnalysis method
    private void performAnalysis(String input) {
        ExpertSystem.SentenceAnalysis analysis = ExpertSystem.analyzeSentence(input);

        if (!analysis.isSentence() && analysis.getSubjects().isEmpty()) {
            updateMessage("Not a sentence", ERROR_COLOR);
            clearVisualization();
            return;
        }

        if (!analysis.isSentence()) {
            updateMessage("Single word identified: " + capitalize(analysis.getSubjects().get(0)), SUCCESS_COLOR);
            displaySingleImage(analysis.getSubjects().get(0));
        } else {
            updateMessage("Analysis complete! " + analysis.getSentenceType() + " sentence identified.", SUCCESS_COLOR);
            updateAnalysisDisplay(analysis);

            List<String> subjects = analysis.getSubjects();
            List<String> objects = analysis.getObjects();

            // Check if we have multiple subjects or objects
            if (subjects.size() > 1 || objects.size() > 1) {
                displayMultipleComponents(subjects, objects);
            } else {
                // Single subject and object
                String subject = subjects.isEmpty() ? null : subjects.get(0);
                String object = objects.isEmpty() ? null : objects.get(0);
                displaySubjectAndObject(subject, object);
            }
        }
    }
    private JPanel ComponentPanel(String word, String type) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
        
        // Create label
        JLabel typeLabel = new JLabel(type + ": " + capitalize(word), SwingConstants.CENTER);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
        panel.add(typeLabel, BorderLayout.NORTH);
        
        // Create image
        ImageIcon icon = ImageMapper.getImageIcon(word);  // Utility class that maps words to image icons
        if (icon != null) {
            Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Scaling image for consistency
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            panel.add(imageLabel, BorderLayout.CENTER);
        } else {
            JLabel placeholderLabel = new JLabel("No image available", SwingConstants.CENTER);
            placeholderLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            placeholderLabel.setForeground(darkMode ? DARK_ACCENT_COLOR : SECONDARY_COLOR);
            panel.add(placeholderLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }
    private void displayMultipleComponents(List<String> subjects, List<String> objects) {
        clearVisualization();
        
        // Change GridLayout to 1 row and 2 columns for side-by-side display
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));  // 1 row, 2 columns
        mainPanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);

        // Add subjects panel
        if (!subjects.isEmpty()) {
            mainPanel.add(MultipleComponentPanel(subjects, "Subjects"));
        }

        // Add objects panel
        if (!objects.isEmpty()) {
            mainPanel.add(MultipleComponentPanel(objects, "Objects"));
        }

        visualizationPanel.add(mainPanel, BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }


    private JPanel MultipleComponentPanel(List<String> words, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
        
        // Add type label
        JLabel typeLabel = new JLabel(type, SwingConstants.CENTER);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        typeLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
        panel.add(typeLabel, BorderLayout.NORTH);
        
        // Create flow panel for images
        JPanel imagesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        imagesPanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
        
        for (String word : words) {
            JPanel wordPanel = new JPanel(new BorderLayout(5, 5));
            wordPanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
            
            ImageIcon icon = ImageMapper.getImageIcon(word);
            if (icon != null) {
                Image scaledImage = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                wordPanel.add(imageLabel, BorderLayout.CENTER);
            }
            
            JLabel wordLabel = new JLabel(capitalize(word), SwingConstants.CENTER);
            wordLabel.setForeground(darkMode ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            wordPanel.add(wordLabel, BorderLayout.SOUTH);
            
            imagesPanel.add(wordPanel);
        }
        
        panel.add(imagesPanel, BorderLayout.CENTER);
        return panel;
    }
    private void clearVisualization() {
        visualizationPanel.removeAll();
        visualizationPanel.add(messageLabel, BorderLayout.NORTH);
    }

    private void displaySingleImage(String word) {
        clearVisualization();
        
        JPanel panel = ComponentPanel(word, "Word");
        visualizationPanel.add(panel, BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }


    private void displaySubjectAndObject(String subject, String object) {
        clearVisualization();

        // Panel to hold the images side by side
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Add subject
        if (subject != null) {
            JPanel subjectPanel = new JPanel(new BorderLayout());
            subjectPanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);

            // Add subject label
            JLabel subjectLabel = new JLabel("Subject: " + capitalize(subject), SwingConstants.CENTER);
            subjectLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
            subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            subjectPanel.add(subjectLabel, BorderLayout.NORTH);

            // Add subject image
            ImageIcon subjectIcon = ImageMapper.getImageIcon(subject);
            if (subjectIcon != null) {
                Image scaledImage = subjectIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                JLabel subjectImageLabel = new JLabel(new ImageIcon(scaledImage));
                subjectImageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                subjectPanel.add(subjectImageLabel, BorderLayout.CENTER);
            }

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);
            imagePanel.add(subjectPanel, gbc);
        }

        // Add object
        if (object != null) {
            JPanel objectPanel = new JPanel(new BorderLayout());
            objectPanel.setBackground(darkMode ? DARK_BG_COLOR : Color.WHITE);

            // Add object label
            JLabel objectLabel = new JLabel("Object: " + capitalize(object), SwingConstants.CENTER);
            objectLabel.setForeground(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR);
            objectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            objectPanel.add(objectLabel, BorderLayout.NORTH);

            // Add object image
            ImageIcon objectIcon = ImageMapper.getImageIcon(object);
            if (objectIcon != null) {
                Image scaledImage = objectIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                JLabel objectImageLabel = new JLabel(new ImageIcon(scaledImage));
                objectImageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                objectPanel.add(objectImageLabel, BorderLayout.CENTER);
            }

            gbc.gridx = 1;
            gbc.gridy = 0;
            imagePanel.add(objectPanel, gbc);
        }

        // Add the image panel to visualization panel
        visualizationPanel.add(imagePanel, BorderLayout.CENTER);
        visualizationPanel.revalidate();
        visualizationPanel.repaint();
    }

    private void updateAnalysisDisplay(ExpertSystem.SentenceAnalysis analysis) {
        StringBuilder html = new StringBuilder("<html><div style='padding: 10px; text-align: center;'>");
        html.append("<table style='margin: 0 auto; border-collapse: collapse; width: 100%; table-layout: fixed;'>");

        // Header row
        html.append("<tr>");
        html.append("<th style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR)).append("; font-size: 14px; width: 30%;'>Sentence Type</th>");
        html.append("<th style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR)).append("; font-size: 14px; width: 20%;'>Mood</th>");
        html.append("<th style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR)).append("; font-size: 14px; width: 20%;'>Subject(s)</th>");
        html.append("<th style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR)).append("; font-size: 14px; width: 20%;'>Verb(s)</th>");
        html.append("<th style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_PRIMARY_COLOR : PRIMARY_COLOR)).append("; font-size: 14px; width: 20%;'>Object(s)</th>");
        html.append("</tr>");

        // Content row
        html.append("<tr>");

        // Sentence Type
        html.append("<td style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_SECONDARY_COLOR : SECONDARY_COLOR))
            .append("; font-size: 12px;'>⚡ ").append(capitalize(analysis.getSentenceType()))
            .append(" Sentence</td>");

        // Mood
        html.append("<td style='padding: 5px; color: ").append(toHexColor(darkMode ? DARK_SECONDARY_COLOR : SECONDARY_COLOR))
            .append("; font-size: 12px;'>").append(capitalize(analysis.getMood()))
            .append("</td>");

        // Subjects
        html.append("<td style='padding: 5px; color: ").append(toHexColor(SUCCESS_COLOR))
            .append("; font-size: 12px;'>").append(analysis.getSubjects().isEmpty() ? "N/A" : String.join(", ", analysis.getSubjects()))
            .append("</td>");

        // Verbs
        html.append("<td style='padding: 5px; color: ").append(toHexColor(WARNING_COLOR))
            .append("; font-size: 12px;'>").append(analysis.getVerbs().isEmpty() ? "N/A" : String.join(", ", analysis.getVerbs()))
            .append("</td>");

        // Objects
        html.append("<td style='padding: 5px; color: ").append(toHexColor(SECONDARY_COLOR))
            .append("; font-size: 12px;'>").append(analysis.getObjects().isEmpty() ? "N/A" : String.join(", ", analysis.getObjects()))
            .append("</td>");

        html.append("</tr>");
        html.append("</table></div></html>");
        analysisLabel.setText(html.toString());
    }


    private void updateImage(String object) {
        if (object == null || object.trim().isEmpty()) {
            imageLabel.setIcon(null);
            return;
        }

        ImageIcon icon = ImageMapper.getImageIcon(object);
        if (icon != null) {
            Image scaledImage = icon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setIcon(null);
            showError("Image not found for: " + object);
        }
    }

    private void updateMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    private void showError(String message) {
        updateMessage("⚠️ " + message, ERROR_COLOR);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    private String toHexColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
}