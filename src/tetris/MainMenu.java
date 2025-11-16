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

    public MainMenu(WindowGame windowGame) {
        this.windowGame = windowGame;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        // Initialize button bounds
        playButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 250, 200, 60);
        exitButton = new Rectangle(WindowGame.WIDTH / 2 - 100, 330, 200, 60);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Title with gradient effect
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(255, 100, 255));
        g.setFont(new Font(font, Font.BOLD, 60));
        g.drawString("TETRIS", WindowGame.WIDTH / 2 - 120, 130);

        g.setColor(new Color(200, 50, 200));
        g.setFont(new Font(font, Font.BOLD, 60));
        g.drawString("TETRIS", WindowGame.WIDTH / 2 - 118, 128);

        // Subtitle
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(font, Font.ITALIC, 18));
        g.drawString("Classic Block Game", WindowGame.WIDTH / 2 - 90, 165);

        // Play Button
        if (playButton.contains(mouseX, mouseY)) {
            g.setColor(new Color(100, 220, 100));
            g.fillRect(playButton.x - 2, playButton.y - 2, playButton.width + 4, playButton.height + 4);
        }
        g.setColor(new Color(50, 180, 50));
        g.fillRect(playButton.x, playButton.y, playButton.width, playButton.height);
        g.setColor(Color.WHITE);
        g.drawRect(playButton.x, playButton.y, playButton.width, playButton.height);
        g.setFont(new Font(font, Font.BOLD, 32));
        g.drawString("PLAY", playButton.x + 55, playButton.y + 42);

        // Exit Button
        if (exitButton.contains(mouseX, mouseY)) {
            g.setColor(new Color(220, 100, 100));
            g.fillRect(exitButton.x - 2, exitButton.y - 2, exitButton.width + 4, exitButton.height + 4);
        }
        g.setColor(new Color(180, 50, 50));
        g.fillRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setColor(Color.WHITE);
        g.drawRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setFont(new Font(font, Font.BOLD, 32));
        g.drawString("EXIT", exitButton.x + 55, exitButton.y + 42);

        // Instructions box
        g.setColor(new Color(50, 50, 50));
        g.fillRect(50, 420, 345, 140);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(50, 420, 345, 140);

        g.setColor(Color.WHITE);
        g.setFont(new Font(font, Font.BOLD, 16));
        g.drawString("HOW TO PLAY:", 160, 445);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(font, Font.PLAIN, 14));
        g.drawString("← → / A D  :  Move Left/Right", 80, 475);
        g.drawString("↓ / S        :  Speed Up", 80, 500);
        g.drawString("↑ / W       :  Rotate Block", 80, 525);
        g.drawString("Clear lines to score points!", 100, 550);
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