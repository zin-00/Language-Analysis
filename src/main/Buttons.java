
package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Buttons extends JFrame implements ActionListener {
    // Color Constants
    private static final Color MAIN_COLOR = new Color(70, 130, 180);
    private static final Color HOVER_COLOR = new Color(100, 149, 237);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color DARK_BG_COLOR = new Color(30, 30, 30);
    private static final Color DARK_MAIN_COLOR = new Color(60, 120, 170);
    private static final Color DARK_HOVER_COLOR = new Color(90, 139, 227);

    // UI Components
    private JButton playButton, optionButton, exitButton;
    private Game puzzleGame;
    private boolean darkMode = false;
    private JComboBox<String> themeComboBox;
    private JCheckBox musicToggleCheckBox;
    private JDialog settingsDialog;
    
    // Animation and Sound
    private SoundManager sound;
    private List<AnimatedElement> backgroundElements;
    private Timer animationTimer;
    private Random random;

    public Buttons() {
        this.sound = new SoundManager();
        this.backgroundElements = new ArrayList<>();
        this.random = new Random();
        
        // Undecorated frame with custom shape
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 800, 600, 20, 20));
        
        initializeMenuUI();
        setVisible(true);
        fadeIn();
        sound.playBackgroundMusic();
        startBackgroundAnimation();
    }

    private void initializeMenuUI() {
        setTitle("Language Analysis Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel with Advanced Animated Background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient Background
                int w = getWidth();
                int h = getHeight();
                Color startColor = darkMode ? DARK_BG_COLOR : BACKGROUND_COLOR;
                Color endColor = darkMode ? new Color(40, 40, 40) : new Color(230, 240, 250);
                GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Draw Animated Background Elements
                for (AnimatedElement element : backgroundElements) {
                    element.draw(g2d);
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Title with Enhanced Typography
        JLabel titleLabel = TitleLabel("Language Analysis Game");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        

        // Buttons with Enhanced Styling
        playButton = StyledButton("Play Game", "PLAY");
        optionButton = StyledButton("Settings", "SETTINGS");
        exitButton = StyledButton("Exit", "EXIT");

        // Layout Setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy++;
        mainPanel.add(playButton, gbc);

        gbc.gridy++;
        mainPanel.add(optionButton, gbc);

        gbc.gridy++;
        mainPanel.add(exitButton, gbc);
    }

    private JLabel TitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Soft Shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(getText(), 3, getHeight() - 4);
                
                // Gradient Text Color
                GradientPaint textGradient = new GradientPaint(
                    0, 0, darkMode ? DARK_MAIN_COLOR : MAIN_COLOR, 
                    getWidth(), getHeight(), 
                    darkMode ? DARK_HOVER_COLOR : HOVER_COLOR
                );
                g2d.setPaint(textGradient);
                g2d.drawString(getText(), 0, getHeight() - 7);
            }
        };
        label.setFont(new Font("Segoe UI", Font.BOLD, 36));
        label.setPreferredSize(new Dimension(400, 80));
        return label;
    }

    private JButton StyledButton(String text, String actionCommand) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D.Float shape = new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, 15, 15
                );
                
                // Dynamic Button Color
                Color baseColor = darkMode ? DARK_MAIN_COLOR : MAIN_COLOR;
                Color hoverColor = darkMode ? DARK_HOVER_COLOR : HOVER_COLOR;
                
                g2d.setColor(getModel().isPressed() ? baseColor.darker() 
                    : getModel().isRollover() ? hoverColor : baseColor);
                
                g2d.fill(shape);
                
                // Text Rendering
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), 
                    (getWidth() - g2d.getFontMetrics().stringWidth(getText())) / 2, 
                    (getHeight() + g2d.getFontMetrics().getAscent()) / 2 - 4
                );
            }
        };
        
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        return button;
    }

    private void showSettingsDialog() {
        settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(400, 300);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new GridBagLayout());
        settingsDialog.getContentPane().setBackground(darkMode ? DARK_BG_COLOR : BACKGROUND_COLOR);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(darkMode ? DARK_BG_COLOR : BACKGROUND_COLOR);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Theme Selection
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(darkMode ? Color.WHITE : Color.BLACK);
        String[] themes = {"Light", "Dark"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedIndex(darkMode ? 1 : 0);

        // Music Toggle
        musicToggleCheckBox = new JCheckBox("Enable Background Music");
        musicToggleCheckBox.setSelected(true);
        musicToggleCheckBox.setBackground(darkMode ? DARK_BG_COLOR : BACKGROUND_COLOR);
        musicToggleCheckBox.setForeground(darkMode ? Color.WHITE : Color.BLACK);

        JButton saveButton = StyledButton("Save", "SAVE");
        JButton cancelButton = StyledButton("Cancel", "CANCEL");

        // Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsPanel.add(themeLabel, gbc);
        gbc.gridy++;
        settingsPanel.add(themeComboBox, gbc);
        gbc.gridy++;
        settingsPanel.add(musicToggleCheckBox, gbc);
        gbc.gridy++;
        settingsPanel.add(saveButton, gbc);
        gbc.gridy++;
        settingsPanel.add(cancelButton, gbc);

        settingsDialog.add(settingsPanel);
        settingsDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "PLAY":
                fadeOut(() -> {
                    setVisible(false);
                    puzzleGame = new Game(this, darkMode);
                    puzzleGame.setVisible(true);
                    sound.stopBackgroundMusic();
                });
                break;
            case "SETTINGS":
                showSettingsDialog();
                break;
            case "EXIT":
                fadeOut(() -> System.exit(0));
                break;
            case "SAVE":
                darkMode = themeComboBox.getSelectedIndex() == 1;
                
                // Handle Music Toggle
                if (musicToggleCheckBox.isSelected()) {
                    sound.playBackgroundMusic();
                } else {
                    sound.stopBackgroundMusic();
                }
                
                settingsDialog.dispose();
                repaint();
                break;
            case "CANCEL":
                settingsDialog.dispose();
                break;
        }
    }

    private void startBackgroundAnimation() {
        animationTimer = new Timer(50, e -> {
            updateBackgroundElements();
            repaint();
        });
        animationTimer.start();
    }

    private void updateBackgroundElements() {
        // Occasionally add new elements
        if (random.nextInt(50) < 5) {
            backgroundElements.add(new AnimatedElement(getWidth(), getHeight()));
        }

        // Update and remove off-screen elements
        backgroundElements.removeIf(element -> {
            element.update();
            return element.isOffScreen();
        });
    }

    private void fadeIn() {
      setOpacity(0.0f);
      Timer fadeTimer = new Timer(50, new ActionListener() {
          private float opacity = 0.0f;
          @Override
          public void actionPerformed(ActionEvent e) {
              opacity += 0.1f;
              if (opacity > 1.0f) {
                  opacity = 1.0f;
                  ((Timer) e.getSource()).stop();
              }
              setOpacity(opacity);
          }
      });
      fadeTimer.start();
  }

  private void fadeOut(Runnable onComplete) {
      Timer fadeTimer = new Timer(50, new ActionListener() {
          private float opacity = 1.0f;
          @Override
          public void actionPerformed(ActionEvent e) {
              opacity -= 0.1f;
              if (opacity < 0.0f) {
                  opacity = 0.0f;
                  ((Timer) e.getSource()).stop();
                  onComplete.run();
              }
              setOpacity(opacity);
          }
      });
      fadeTimer.start();
  }
    private class AnimatedElement {
        private String text;
        private float x, y;
        private float speed;
        private float alpha;
        private Color color;
        private Font font;

        public AnimatedElement(int maxWidth, int maxHeight) {
            this.text = getRandomLanguageTerm();
            this.x = random.nextFloat() * maxWidth;
            this.y = -50;  // Start above the visible area
            this.speed = random.nextFloat() * 2 + 0.5f;
            this.alpha = 0.1f + random.nextFloat() * 0.5f;
            this.color = generateSoftColor();
            this.font = new Font("Segoe UI", Font.PLAIN, random.nextInt(12) + 10);
        }

        private Color generateSoftColor() {
            Color[] softColors = {
                new Color(70, 130, 180, 100),
                new Color(100, 149, 237, 100),
                new Color(60, 120, 170, 100),
                new Color(90, 139, 227, 100)
            };
            return softColors[random.nextInt(softColors.length)];
        }

        public void update() {
            y += speed;
        }

        public void draw(Graphics2D g2d) {
            g2d.setFont(font);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
            g2d.drawString(text, x, y);
        }

        public boolean isOffScreen() {
            return y > 700;  // Adjusted for typical window height
        }

        private String getRandomLanguageTerm() {
            String[] terms = {
                "Syntax", "Semantics", "Morphology", "Phonology", "Grammar", 
                "Lexicon", "Discourse", "Pragmatics", "Etymology", "Linguistics"
            };
            return terms[random.nextInt(terms.length)];
        }
    }

    // Reopen menu method remains the same
    public void reOpenMenu() {
        setVisible(true);
        sound.playBackgroundMusic();
        fadeIn();
    }
}
