package tetris;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class MainMenu extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private WindowGame windowGame;
    private int mouseX, mouseY;

    private Rectangle playButton;
    private Rectangle exitButton;

    private String font = "VERDANA";
    private static FileManager fileManager;
    private static Font fontRegular;
    private static Font fontBig;
    private static Font fontbig;

    public MainMenu(WindowGame windowGame) {
        this.windowGame = windowGame;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        fileManager = WindowGame.getFileManager();
        try{
            fontRegular = fileManager.loadFont(14f);
            fontBig = fileManager.loadFont(41f);
            fontbig = fileManager.loadFont(30f);
        }catch(Exception e){
            e.printStackTrace();
        }


        // Initialize button bounds
        playButton = new Rectangle(WindowGame.WIDTH / 2 - 120, 270, 240, 70);
        exitButton = new Rectangle(WindowGame.WIDTH / 2 - 120, 360, 240, 70);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Modern gradient background
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(15, 12, 41),
                0, getHeight(), new Color(48, 43, 99)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Decorative circles in background
        g2d.setColor(new Color(138, 43, 226, 30));
        g2d.fillOval(-50, -50, 200, 200);
        g2d.fillOval(WindowGame.WIDTH - 150, 400, 250, 250);

        g2d.setColor(new Color(65, 105, 225, 20));
        g2d.fillOval(WindowGame.WIDTH - 100, -80, 180, 180);

        // Title with modern styling
        g2d.setColor(new Color(255, 255, 255, 230));
        g2d.setFont(fontBig);
        String title = "TETRIS";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (WindowGame.WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 140);

        // Subtitle
        g2d.setColor(new Color(200, 200, 255, 180));
        g2d.setFont(fontRegular);
        String subtitle = "A TIMELESS PUZZLE EXPERIENCE";
        fm = g2d.getFontMetrics();
        int subtitleX = (WindowGame.WIDTH - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, subtitleX, 175);

        // Thin line under title
        g2d.setColor(new Color(138, 43, 226, 150));
        g2d.setFont(fontRegular);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(WindowGame.WIDTH / 2 - 100, 190, WindowGame.WIDTH / 2 + 100, 190);

        // Play Button - Glass morphism style
        g2d.setFont(fontRegular);
        drawGlassButton(g2d, playButton, "PLAY", playButton.contains(mouseX, mouseY),
                new Color(100, 200, 255, 80), new Color(100, 200, 255, 150));


        // Exit Button - Glass morphism style
        drawGlassButton(g2d, exitButton, "EXIT", exitButton.contains(mouseX, mouseY),
                new Color(255, 100, 150, 60), new Color(255, 100, 150, 120));

        // Instructions with glass effect
        int instrY = 470;
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.fillRoundRect(60, instrY, 325, 100, 20, 20);

        // Glass border
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(60, instrY, 325, 100, 20, 20);

        // Instructions text
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.setFont(fontRegular);
        g2d.drawString("CONTROLS", 175, instrY + 25);

        g2d.setColor(new Color(220, 220, 255, 200));
        g2d.setFont(fontRegular);
        g2d.drawString("A D   Move Left/Right", 90, instrY + 50);
        g2d.drawString("S      Speed Up", 90, instrY + 70);
        g2d.drawString("W      Rotate Block", 90, instrY + 90);
    }

    private void drawGlassButton(Graphics2D g2d, Rectangle rect, String text, boolean isHovered, Color baseColor, Color hoverColor) {
        // Button background with glass effect
        if (isHovered) {
            g2d.setColor(hoverColor);
        } else {
            g2d.setColor(baseColor);
        }
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // Top highlight for glass effect
        GradientPaint glassHighlight = new GradientPaint(
                rect.x, rect.y, new Color(255, 255, 255, 60),
                rect.x, rect.y + rect.height / 2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(glassHighlight);
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height / 2, 15, 15);

        // Border
        g2d.setColor(new Color(255, 255, 255, isHovered ? 180 : 120));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

        // Button text with shadow
        g2d.setFont(fontbig);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        // Text shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(text, textX + 2, textY + 2);

        // Text
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.drawString(text, textX, textY);
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
        // Allow Enter key to start game
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            windowGame.startTetris();
        }
        // Allow Escape to exit
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}