package tetris;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Random;

public class MainMenu extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private WindowGame windowGame;
    private int mouseX, mouseY;

    private Rectangle playButton;
    private Rectangle exitButton;

    private static FileManager fileManager;
    private static Font fontRegular;
    private static Font fontBig;
    private static Font fontbig;

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
        playButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 250, 200, 70);
        exitButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 285, 200, 70);


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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dark background
        g2d.setColor(new Color(40, 40, 40));
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw floating blocks
        for (FloatingBlock block : floatingBlocks) {
            block.draw(g2d);
        }

        // Draw title with gradient and chromatic effect
        drawChromaticTitle(g2d);

        // Subtitle
        g2d.setColor(new Color(255, 100, 200));
        g2d.setFont(fontbig);
        String subtitle = "CLASSIC BOARD GAME";
        FontMetrics fm = g2d.getFontMetrics();
        int subtitleX = (WindowGame.WIDTH - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, subtitleX, 230);

        // Play Button with chromatic effect
        drawChromaticButton(g2d, playButton, "PLAY", playButton.contains(mouseX, mouseY));

        // Exit Button with chromatic effect
        drawChromaticButton(g2d, exitButton, "EXIT", exitButton.contains(mouseX, mouseY));

        //Instructions section with glass effect
        int instrY = 410;
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillRoundRect(40, instrY, 365, 150, 20, 20);

        // Glass border
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(40, instrY, 365, 150, 20, 20);

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
        g2d.drawString("CONTROLS:", 60, instrY + 73);

        g2d.setColor(new Color(220, 220, 255, 200));
        g2d.setFont(fontBig);
        g2d.drawString("A / D -  Move Left/Right", 75, instrY + 93);
        g2d.drawString("W -  Rotate Block", 75, instrY + 111);
        g2d.drawString("S -  Speed Up Drop", 75, instrY + 128);

        // Additional tip
        g2d.setColor(new Color(255, 200, 100, 180));
        g2d.setFont(fontBig);
        String tip = "TIP: Complete lines to score points!";
        fm = g2d.getFontMetrics();
        int tipX = (WindowGame.WIDTH - fm.stringWidth(tip)) / 2;
        g2d.drawString(tip, tipX, instrY + 145);

    }

    private void drawChromaticTitle(Graphics2D g2d) {
        String title = "TETRIS";
        g2d.setFont(fontRegular);
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WindowGame.WIDTH - fm.stringWidth(title)) / 2;
        int titleY = 160;

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

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        if (playButton.contains(mouseX, mouseY)) {
            windowGame.startTetris();
        }

        if (exitButton.contains(mouseX, mouseY)) {
            System.exit(0);
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
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            windowGame.startTetris();
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}