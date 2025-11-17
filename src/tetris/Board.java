package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import audio.SoundManager;

public class Board extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    private BufferedImage menuIcon;
    private Rectangle menuBounds;
    private WindowGame windowGame;
    private int score = 0;
    private String font = "VERDANA";

    private static int FPS = 60;
    private static int delay = 1000 / FPS;

    public static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private Timer looper;
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private boolean gameOver = false;
    private boolean gamePaused = false;

    private int mouseX, mouseY;

    private BufferedImage pause, refresh, soundOnIcon, soundOffIcon;
    private Rectangle stopBounds, refreshBounds, soundBounds;
    private boolean soundEnabled = true;

    private Random random;

    //Array of Colors
    private Color[] colors = {Color.decode("#ed1c24"),Color.decode("#ff7f27"),Color.decode("#fff200"),
            Color.decode("#22b14c"),Color.decode("#00a2e8"),Color.decode("#a349a4"),Color.decode("#3f48cc")};

    //Array of Shape
    private Shape[] shapes = new Shape[8];

    private Shape currentShape, nextShape;

    public Board(WindowGame windowGame) {
        this.windowGame = windowGame;

        setFocusable(true);
        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);

        pause = ImageLoader.loadImage("/pause.png", 90 , 90 );
        refresh = ImageLoader.loadImage("/refresh.png", 70, 70);
        soundOnIcon = ImageLoader.loadImage("/soundOn.png", 70, 70);
        soundOffIcon = ImageLoader.loadImage("/soundOff.png", 70, 70);

        // menu button icon
        menuIcon = new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = menuIcon.createGraphics();
        g2d.setColor(new Color(80, 80, 180));
        g2d.fillRect(0, 0, 70, 70);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, 69, 69);
        g2d.setFont(new Font("VERDANA", Font.BOLD, 14));
        g2d.drawString("MENU", 12, 40);
        g2d.dispose();

        stopBounds = new Rectangle(320, 490, pause.getWidth(), pause.getHeight());
        refreshBounds = new Rectangle(330, 500 - refresh.getHeight() - 10 , refresh.getWidth(), refresh.getHeight());
        soundBounds = new Rectangle(330, 310, 70, 70);
        menuBounds = new Rectangle(330,370,70,70);
        random = new Random();

        shapes[0] = new Shape(new int[][]{ //shape l
                {1,1,1,1}
        }, this, colors[0]);

        shapes[1] = new Shape(new int[][]{ //shape t
                {1,1,1},
                {0,1,0}
        }, this, colors[0]);

        shapes[2] = new Shape(new int[][]{ //shape l1
                {1,1,1},
                {1,0,0}
        }, this, colors[0]);

        shapes[3] = new Shape(new int[][]{ //shape l2
                {1,1,1},
                {0,0,1}
        }, this, colors[0]);

        shapes[4] = new Shape(new int[][]{ //shape s
                {0,1,1},
                {1,1,0}
        }, this, colors[0]);

        shapes[5] = new Shape(new int[][]{ //shape z
                {1,1,0},
                {0,1,1}
        }, this, colors[0]);

        shapes[6] = new Shape(new int[][]{ //shape square
                {1,1},
                {1,1}
        }, this, colors[0]);

        shapes[7] = new Shape(new int[][]{ //shape l
                {1},
                {1},
                {1},
                {1}
        }, this, colors[0]);

        currentShape = shapes[0];

        looper = new Timer(delay, new ActionListener() {
            int n = 0;
            @Override
            public void  actionPerformed(ActionEvent e){
                if (gameOver) {
                    repaint();
                    return;
                }
                update();
                repaint();
            }
        });
        looper.start();
    }


    private void update() {
        if(gameOver || gamePaused) {
            return;
        }
            currentShape.update();
    }


    private void checkGameOver() {
        int[][] coords = currentShape.getCoords();
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    if (board[row + currentShape.getY()][col + currentShape.getX()] != null) {
                        gameOver = true;
                    }
                }
            }
        }
    }
    public Color[][] getBoard() {
        return board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // draw current shape
        currentShape.render(g);

        if (stopBounds.contains(mouseX, mouseY)) {
            g.drawImage(pause.getScaledInstance(pause.getWidth() + 3, pause.getHeight() + 3, BufferedImage.SCALE_DEFAULT), stopBounds.x + 3, stopBounds.y + 3, null);
        } else {
            g.drawImage(pause, stopBounds.x, stopBounds.y, null);
        }

        if (refreshBounds.contains(mouseX, mouseY)) {
            g.drawImage(refresh.getScaledInstance(refresh.getWidth() + 3, refresh.getHeight() + 3,
                    BufferedImage.SCALE_DEFAULT), refreshBounds.x + 3, refreshBounds.y + 3, null);
        } else {
            g.drawImage(refresh, refreshBounds.x, refreshBounds.y, null);
        }

        BufferedImage currentSoundIcon = soundEnabled ? soundOnIcon : soundOffIcon;

        if (soundBounds.contains(mouseX, mouseY)) {
            g.drawImage(currentSoundIcon.getScaledInstance(70, 70, BufferedImage.SCALE_SMOOTH),
                    soundBounds.x + 3, soundBounds.y + 3, null);
        } else {
            g.drawImage(currentSoundIcon, soundBounds.x, soundBounds.y, null);
        }

        // draw menu button
        if (menuBounds.contains(mouseX, mouseY)) {
            g.drawImage(menuIcon.getScaledInstance(73, 73, BufferedImage.SCALE_SMOOTH),
                    menuBounds.x - 2, menuBounds.y - 2, null);
        } else {
            g.drawImage(menuIcon, menuBounds.x, menuBounds.y, null);
        }



        // draw board block
        for(int row = 0; row < board.length; row++){
            for(int col = 0; col<board[row].length; col++){
                if(board[row][col] != null){
                    g.setColor(board[row][col]);
                    g.fillRect(col * BLOCK_SIZE,row*BLOCK_SIZE,BLOCK_SIZE,BLOCK_SIZE);
                }

            }
        }

        // draw grid line
        g.setColor(Color.black);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            g.drawLine(0, BLOCK_SIZE * row,BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        }

        g.setColor(Color.black);
        for (int col = 0; col < BOARD_WIDTH; col++) {
            g.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
        }

        Graphics2D g2d = (Graphics2D) g.create();

        // Define a dashed stroke: {dash length, gap length}
        float[] dashPattern = {6f, 8f}; // Adjust these values to control dot size and spacing
        Stroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f);

        g2d.setStroke(dashed);

        g2d.setColor(Color.darkGray);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            g2d.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        }
        for (int col = 0; col < BOARD_WIDTH; col++) {
            g2d.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
        }
        g2d.dispose();


        // draw border around the grid
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE);

        //draw the score
        g.setFont(new Font(font, Font.BOLD, 25));
        g.drawString("SCORE", WindowGame.WIDTH - 125, WindowGame.HEIGHT - 600 );
        g.drawString(score + "", WindowGame.WIDTH - 90, WindowGame.HEIGHT - 570);

        if (gamePaused) {
            String gamePausedString = "GAME PAUSED";
            g.setColor(Color.WHITE);
            g.setFont(new Font(font, Font.BOLD, 25));
            g.drawString(gamePausedString, 50, WindowGame.HEIGHT /2);
        }
        if (gameOver) {
            String gameOverString2 = "GAME OVER";
            g.setColor(Color.magenta);
            g.setFont(new Font(font, Font.BOLD, 30));
            g.drawString(gameOverString2, 50, WindowGame.HEIGHT / 2);
        }
    }

    public void setNextShape() {
        int index = random.nextInt(shapes.length);
        int colorIndex = random.nextInt(colors.length);
        nextShape = new Shape(shapes[index].getCoords(), this, colors[colorIndex]);
    }

    public void setCurrentShape() {
        // Pick a random shape type index
        int shapeIndex = random.nextInt(shapes.length);

        // Get the shape pattern from shapes array at that index
        int[][] shapePattern = shapes[shapeIndex].getPattern();

        // Assign a new Shape with the same pattern but a new random color
        currentShape = new Shape(shapePattern, this, colors[random.nextInt(colors.length)]);

        currentShape.reset();
        checkGameOver();
    }



    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            currentShape.speedUp();
        } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            currentShape.moveRight();
        } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            currentShape.moveLeft();
        } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            currentShape.rotateShape();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            currentShape.speedDown();
        }
    }
    public void addScore(int score) {
        this.score += score;
    }

    public void startGame() {
        stopGame();
        setNextShape();
        setCurrentShape();
        gameOver = false;
        looper.start();

    }

    public void stopGame() {
        score = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }
        looper.stop();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if(stopBounds.contains(mouseX, mouseY)) {
            gamePaused = !gamePaused;
        }
        if(refreshBounds.contains(mouseX,mouseY)) {
            startGame();
        }
        if (soundBounds.contains(mouseX, mouseY)) {
            soundEnabled = !soundEnabled;
            if (soundEnabled){
                SoundManager.stopAll();
                SoundManager.playLoop("sfx/Tetris.wav");
            }else{
                SoundManager.stop("sfx/Tetris.wav");
            }
        }
        if (menuBounds.contains(mouseX, mouseY)) {
            windowGame.returnToMenu();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


}

