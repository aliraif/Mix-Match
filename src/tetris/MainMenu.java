package tetris;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import audio.SoundManager;


public class MainMenu extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private WindowGame windowGame;
    private int mouseX, mouseY;

    private Rectangle playButton;
    private Rectangle exitButton;

    private Rectangle gameModeButton;
    private Rectangle soundBounds;

    // Game mode selection buttons
    private Rectangle classicModeButton;
    private Rectangle careerModeButton;
    private Rectangle endlessModeButton;
    private Rectangle backButton;

    // Track which screen we're on
    private boolean showingModeSelection = false;
    private String selectedMode = GameMode.CLASSIC;
    private boolean soundEnabled = true;

    private static FileManager fileManager;
    private static Font fontRegular;
    private static Font fontBig;
    private static Font fontbig;

    private BufferedImage soundOnIcon, soundOffIcon;

    private ArrayList<FloatingBlock> floatingBlocks;
    private Random random;

    public MainMenu(WindowGame windowGame) {
        this.windowGame = windowGame;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        fileManager = WindowGame.getFileManager();
        try{
            fontRegular = fileManager.loadFont(32f);
            fontBig = fileManager.loadFont(12f);
            fontbig = fileManager.loadFont(20f);
        }catch(Exception e){
            e.printStackTrace();
        }

        // Initialize button bounds
        playButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 210, 200, 45);
        gameModeButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 250, 200, 45);
        exitButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 290, 200, 45);
        soundBounds = new Rectangle(WindowGame.WIDTH - 70, 20, 50, 50);

        // Initialize game mode selection button bounds
        classicModeButton = new Rectangle(WindowGame.WIDTH / 2 - 150, 200, 300, 60);
        careerModeButton = new Rectangle(WindowGame.WIDTH / 2 - 150, 270, 300, 60);
        endlessModeButton = new Rectangle(WindowGame.WIDTH / 2 - 150, 340, 300, 60);
        backButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 480, 200, 50);


        // Initialize floating blocks
        random = new Random();
        floatingBlocks = new ArrayList<>();
        createFloatingBlocks();

        // Start animation thread
        new Thread(() -> {
            while (true) {
                updateFloatingBlocks();
                repaint();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void createFloatingBlocks() {
        // Create floating tetris blocks in background
        for (int i = 0; i < 12; i++) {
            floatingBlocks.add(new FloatingBlock(random));
        }
    }

    private void updateFloatingBlocks() {
        for (FloatingBlock block : floatingBlocks) {
            block.update();
        }
    }

    private void drawSoundIcon(Graphics2D g2d, int x, int y, int size, boolean soundOn, boolean isHovered) {
        // Scale factor for hover effect
        int drawSize = isHovered ? size + 5 : size;
        int offset = isHovered ? -2 : 0;

        // Calculate center position
        int centerX = x + offset + drawSize / 2;
        int centerY = y + offset + drawSize / 2;

        // Set color to white for the icon
        g2d.setColor(Color.WHITE);

        // Draw speaker body (small rectangle)
        g2d.fillRect(centerX - 10, centerY - 5, 6, 10);

        // Draw speaker horn (triangle shape)
        int[] hornX = {centerX - 4, centerX + 1, centerX + 1, centerX - 4};
        int[] hornY = {centerY - 7, centerY - 11, centerY + 11, centerY + 7};
        g2d.fillPolygon(hornX, hornY, 4);

        if (soundOn) {
            // Draw sound waves (3 arcs)
            g2d.setStroke(new BasicStroke(2f));

            // First wave (small)
            g2d.drawArc(centerX + 1 - 4, centerY - 4, 8, 8, -45, 90);

            // Second wave (medium)
            g2d.drawArc(centerX + 1 - 7, centerY - 7, 14, 14, -45, 90);

            // Third wave (large)
            g2d.drawArc(centerX + 1 - 10, centerY - 10, 20, 20, -45, 90);
        } else {
            // Draw X mark for mute
            g2d.setStroke(new BasicStroke(2.5f));

            // First diagonal of X
            g2d.drawLine(centerX + 5, centerY - 6, centerX + 13, centerY + 6);

            // Second diagonal of X
            g2d.drawLine(centerX + 13, centerY - 6, centerX + 5, centerY + 6);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark background
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw floating blocks
        for (FloatingBlock block : floatingBlocks) {
            block.draw(g2d);
        }

        // Draw sound button
        boolean isHovered = soundBounds.contains(mouseX, mouseY);
        drawSoundIcon(g2d, soundBounds.x, soundBounds.y, 50, soundEnabled, isHovered);

        if (showingModeSelection) {
            drawModeSelection(g2d);
        } else {
            drawMainMenu(g2d);
        }
    }

    private void drawChromaticTitle(Graphics2D g2d) {
        String title = "TETRIS";
        g2d.setFont(fontRegular);
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WindowGame.WIDTH - fm.stringWidth(title)) / 2;
        int titleY = 120;

        // Chromatic aberration effect - cyan shadow
        g2d.setColor(new Color(0, 255, 255, 100));
        g2d.drawString(title, titleX - 3, titleY - 2);

        // Magenta shadow
        g2d.setColor(new Color(255, 0, 255, 100));
        g2d.drawString(title, titleX + 3, titleY + 2);

        // Main title with gradient
        GradientPaint titleGradient = new GradientPaint(
                titleX, titleY - 40, new Color(138, 43, 226),
                titleX, titleY + 10, new Color(255, 0, 200)
        );
        g2d.setPaint(titleGradient);
        g2d.drawString(title, titleX, titleY);

        // Bright outline
        g2d.setColor(new Color(0, 255, 255));
        g2d.setStroke(new BasicStroke(2f));

        // Draw outline by drawing at slight offsets
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    g2d.drawString(title, titleX + i, titleY + j);
                }
            }
        }

        // Draw main title again on top
        g2d.setPaint(titleGradient);
        g2d.drawString(title, titleX, titleY);
    }

    private void drawChromaticButton(Graphics2D g2d, Rectangle rect, String text, boolean isHovered) {
        g2d.setFont(fontbig);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        if (isHovered) {
            // Chromatic aberration effect when hovered
            // Cyan shadow
            g2d.setColor(new Color(0, 255, 255, 150));
            g2d.drawString(text, textX - 2, textY - 1);

            // Magenta shadow
            g2d.setColor(new Color(255, 0, 255, 150));
            g2d.drawString(text, textX + 2, textY + 1);
        }

        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }

    // Inner class for floating blocks
    private class FloatingBlock {
        float x, y;
        float speedX, speedY;
        float rotation;
        float rotationSpeed;
        int size;
        Color color;
        int[][] shape;

        public FloatingBlock(Random random) {
            x = random.nextInt(WindowGame.WIDTH);
            y = random.nextInt(WindowGame.HEIGHT);
            speedX = (random.nextFloat() - 0.5f) * 1.5f;
            speedY = (random.nextFloat() - 0.5f) * 1.5f;
            rotation = random.nextFloat() * 360;
            rotationSpeed = (random.nextFloat() - 0.5f) * 2;
            size = 20 + random.nextInt(15);

            // Tetris colors with transparency
            Color[] colors = {
                    new Color(0, 240, 240, 40),  // I - Cyan
                    new Color(240, 160, 0, 40),  // L - Orange
                    new Color(0, 0, 240, 40),    // J - Blue
                    new Color(240, 240, 0, 40),  // O - Yellow
                    new Color(0, 240, 0, 40),    // S - Green
                    new Color(160, 0, 240, 40),  // T - Purple
                    new Color(240, 0, 0, 40)     // Z - Red
            };
            color = colors[random.nextInt(colors.length)];

            // Simple block shapes
            int[][][] shapes = {
                    {{1,1,1,1}},           // I
                    {{1,1},{1,0},{1,0}},   // L
                    {{1,1},{0,1},{0,1}},   // J
                    {{1,1},{1,1}},         // O
            };
            shape = shapes[random.nextInt(shapes.length)];
        }

        public void update() {
            x += speedX;
            y += speedY;
            rotation += rotationSpeed;

            // Wrap around screen
            if (x < -100) x = WindowGame.WIDTH + 100;
            if (x > WindowGame.WIDTH + 100) x = -100;
            if (y < -100) y = WindowGame.HEIGHT + 100;
            if (y > WindowGame.HEIGHT + 100) y = -100;
        }

        public void draw(Graphics2D g2d) {
            Graphics2D g2dCopy = (Graphics2D) g2d.create();
            g2dCopy.translate(x, y);
            g2dCopy.rotate(Math.toRadians(rotation));

            g2dCopy.setColor(color);
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] == 1) {
                        g2dCopy.fillRect(col * size - size, row * size - size, size - 2, size - 2);
                    }
                }
            }
            g2dCopy.dispose();
        }
    }

    private void drawMainMenu(Graphics2D g2d) {
        // Draw title with gradient and chromatic effect
        drawChromaticTitle(g2d);

        // Subtitle
        g2d.setColor(new Color(255, 100, 200));
        g2d.setFont(fontbig);
        String subtitle = "CLASSIC BOARD GAME";
        FontMetrics fm = g2d.getFontMetrics();
        int subtitleX = (WindowGame.WIDTH - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, subtitleX, 190);

        // Play Button
        drawChromaticButton(g2d, playButton, "PLAY", playButton.contains(mouseX, mouseY));

        // Game Mode Button (NEW)
        drawChromaticButton(g2d, gameModeButton, "GAME MODE", gameModeButton.contains(mouseX, mouseY));

        // Exit Button
        drawChromaticButton(g2d, exitButton, "EXIT", exitButton.contains(mouseX, mouseY));

        // Current mode indicator
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setFont(fontBig);
        String modeText = "Current Mode: " + GameMode.getDisplayName(selectedMode);
        fm = g2d.getFontMetrics();
        int modeX = (WindowGame.WIDTH - fm.stringWidth(modeText)) / 2;
        g2d.drawString(modeText, modeX, 370);

        // Instructions section with glass effect (moved up and much bigger)
        int instrY = 380;
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillRoundRect(40, instrY, 365, 180, 20, 20);

        // Glass border
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(40, instrY, 365, 180, 20, 20);

        // Instructions header
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.setFont(fontBig);
        String howToPlay = "HOW TO PLAY";
        fm = g2d.getFontMetrics();
        int headerX = (WindowGame.WIDTH - fm.stringWidth(howToPlay)) / 2;
        g2d.drawString(howToPlay, headerX, instrY + 25);

        // Divider line
        g2d.setColor(new Color(138, 43, 226, 100));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(80, instrY + 35, WindowGame.WIDTH - 80, instrY + 35);

        // Game objective
        g2d.setColor(new Color(200, 220, 255, 220));
        g2d.setFont(fontBig);
        g2d.drawString("Stack falling blocks to complete lines", 60, instrY + 50);

        // Controls section
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.setFont(fontBig);
        g2d.drawString("CONTROLS :", 60, instrY + 70);

        g2d.setColor(new Color(220, 220, 255, 200));
        g2d.setFont(fontBig);
        g2d.drawString("A / D -  Move Left/Right", 75, instrY + 90);
        g2d.drawString("W -  Rotate Block", 75, instrY + 110);
        g2d.drawString("S -  Speed Up Drop", 75, instrY + 130);
        g2d.drawString("M - Mute",75, instrY + 150);

        // Additional tip
        g2d.setColor(new Color(255, 200, 100, 180));
        g2d.setFont(fontBig);
        String tip = "TIP: Complete lines to score points!";
        fm = g2d.getFontMetrics();
        int tipX = (WindowGame.WIDTH - fm.stringWidth(tip)) / 2;
        g2d.drawString(tip, tipX, instrY + 170);
    }

    private void drawModeSelection(Graphics2D g2d) {
        // Title
        String title = "SELECT GAME MODE";
        g2d.setFont(fontRegular);
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WindowGame.WIDTH - fm.stringWidth(title)) / 2;

        // Chromatic title effect
        g2d.setColor(new Color(0, 255, 255, 100));
        g2d.drawString(title, titleX - 2, 148);
        g2d.setColor(new Color(255, 0, 255, 100));
        g2d.drawString(title, titleX + 2, 152);

        GradientPaint titleGradient = new GradientPaint(
                titleX, 110, new Color(138, 43, 226),
                titleX, 160, new Color(255, 0, 200)
        );
        g2d.setPaint(titleGradient);
        g2d.drawString(title, titleX, 150);

        // Classic Mode Button
        drawModeButton(g2d, classicModeButton, GameMode.CLASSIC,
                classicModeButton.contains(mouseX, mouseY));

        // Career Mode Button
        drawModeButton(g2d, careerModeButton, GameMode.CAREER,
                careerModeButton.contains(mouseX, mouseY));

        // Endless Mode Button
        drawModeButton(g2d, endlessModeButton, GameMode.ENDLESS,
                endlessModeButton.contains(mouseX, mouseY));

        // Back Button
        drawChromaticButton(g2d, backButton, "BACK", backButton.contains(mouseX, mouseY));
    }

    private void drawModeButton(Graphics2D g2d, Rectangle rect, String mode, boolean isHovered) {
        // Button background with glow effect
        if (mode.equals(selectedMode)) {
            // Highlight selected mode
            g2d.setColor(new Color(138, 43, 226, 100));
            g2d.fillRoundRect(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4, 15, 15);
        }

        if (isHovered) {
            g2d.setColor(new Color(255, 255, 255, 60));
        } else {
            g2d.setColor(new Color(255, 255, 255, 30));
        }
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // Border
        if (mode.equals(selectedMode)) {
            g2d.setColor(new Color(138, 43, 226, 200));
        } else {
            g2d.setColor(new Color(255, 255, 255, 100));
        }
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // Mode name
        g2d.setFont(fontbig);
        FontMetrics fm = g2d.getFontMetrics();
        String name = GameMode.getDisplayName(mode);
        int nameX = rect.x + (rect.width - fm.stringWidth(name)) / 2;
        int nameY = rect.y + 30;

        if (isHovered) {
            // Chromatic effect on hover
            g2d.setColor(new Color(0, 255, 255, 150));
            g2d.drawString(name, nameX - 1, nameY - 1);
            g2d.setColor(new Color(255, 0, 255, 150));
            g2d.drawString(name, nameX + 1, nameY + 1);
        }

        g2d.setColor(Color.WHITE);
        g2d.drawString(name, nameX, nameY);

        // Description
        g2d.setFont(fontBig);
        g2d.setColor(new Color(200, 200, 255, 200));
        String desc = GameMode.getDescription(mode);
        fm = g2d.getFontMetrics();
        int descX = rect.x + (rect.width - fm.stringWidth(desc)) / 2;
        g2d.drawString(desc, descX, rect.y + 50);

        // Selected checkmark
        if (mode.equals(selectedMode)) {
            g2d.setColor(new Color(0, 255, 100));
            g2d.setFont(fontbig);
            g2d.drawString("✓", rect.x + 15, rect.y + 30);
        }
    }

    private void toggleSound() {
        soundEnabled = !soundEnabled;
        if (soundEnabled) {
            SoundManager.uncutAllSound();
        } else {
            SoundManager.cutAllSound();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        if (soundBounds.contains(mouseX, mouseY)){
            toggleSound();
            return;
        }

        if (showingModeSelection) {
            // Handle mode selection screen clicks
            if (classicModeButton.contains(mouseX, mouseY)) {
                selectedMode = GameMode.CLASSIC;
            } else if (careerModeButton.contains(mouseX, mouseY)) {
                selectedMode = GameMode.CAREER;
            } else if (endlessModeButton.contains(mouseX, mouseY)) {
                selectedMode = GameMode.ENDLESS;
            } else if (backButton.contains(mouseX, mouseY)) {
                showingModeSelection = false;
            }
        } else {
            // Handle main menu clicks
            if (playButton.contains(mouseX, mouseY)) {
                windowGame.startTetris(selectedMode);
            } else if (gameModeButton.contains(mouseX, mouseY)) {
                showingModeSelection = true;
            } else if (exitButton.contains(mouseX, mouseY)) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_M) {
            toggleSound();
        }

        if (showingModeSelection) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                showingModeSelection = false;
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                windowGame.startTetris(selectedMode);
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public String getSelectedMode() {
        return selectedMode;
    }
}