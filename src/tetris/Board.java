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
    private String gameMode = GameMode.CLASSIC;
    // Separate state for each game mode
    private int classicScore = 0;
    private int classicHighScore = 0;
    private int classicLinesCleared = 0;
    private boolean classicNewHighScore = false;
    private boolean showingClassicGameOver = false;
    private boolean showingCareerGameOver = false;
    private int endlessScore = 0;
    private int careerScore = 0;
    private Color[][] classicBoard = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private Color[][] endlessBoard = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private Color[][] careerBoard = new Color[BOARD_HEIGHT][BOARD_WIDTH];

    private CareerModeManager careerManager;
    private boolean showingLevelComplete = false;
    private boolean showingCareerComplete = false;

    private EndlessModeManager endlessManager;
    private boolean showingEndlessGameOver = false;

    private static int FPS = 60;
    private static int delay = 1000 / FPS;

    public static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private Timer looper;
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private boolean gameOver = false;
    private boolean gamePaused = false;

    // Separate game over state for each mode
    private boolean classicGameOver = false;
    private boolean endlessGameOver = false;
    private boolean careerGameOver = false;

    // Save current falling shape for each mode
    private int[][] classicShapeCoords = null;
    private Color classicShapeColor = null;
    private int classicShapeX = 0;
    private int classicShapeY = 0;

    private int[][] endlessShapeCoords = null;
    private Color endlessShapeColor = null;
    private int endlessShapeX = 0;
    private int endlessShapeY = 0;

    // Save endless mode timer state
    private long endlessStartTime = 0;
    private long endlessTotalPausedDuration = 0;
    private int endlessLinesCleared = 0;

    private int[][] careerShapeCoords = null;
    private Color careerShapeColor = null;
    private int careerShapeX = 0;
    private int careerShapeY = 0;

    private int mouseX, mouseY;

    private BufferedImage pause, refresh, soundOnIcon, soundOffIcon;
    private Rectangle stopBounds, refreshBounds, soundBounds;
    private boolean soundEnabled = true;

    private Random random;

    private static Font fontRegular;
    private static Font fontBig;
    private static FileManager fileManager;


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

        pause = ImageLoader.loadImage("/pause.png", 60 , 60 );
        refresh = ImageLoader.loadImage("/refresh.png", 60, 60);
        soundOnIcon = ImageLoader.loadImage("/soundOn.png", 60, 60);
        soundOffIcon = ImageLoader.loadImage("/soundOff.png", 60, 60);
        menuIcon = ImageLoader.loadImage("/menu.png", 60, 60);

        stopBounds = new Rectangle(330, 490, pause.getWidth(), pause.getHeight());
        refreshBounds = new Rectangle(330, 500 - refresh.getHeight() - 10 , refresh.getWidth(), refresh.getHeight());
        soundBounds = new Rectangle(330, 310, 70, 70);
        menuBounds = new Rectangle(330,370,70,70);
        random = new Random();

        fileManager = WindowGame.getFileManager();
        careerManager = new CareerModeManager();
        endlessManager = new EndlessModeManager();

        // Initialize separate boards for each mode
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                classicBoard[row][col] = null;
                endlessBoard[row][col] = null;
                careerBoard[row][col] = null;
            }
        }
        try{
            fontRegular = fileManager.loadFont(24f);
            fontBig = fileManager.loadFont(32f);
        }catch(Exception e){
            e.printStackTrace();
        }

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

        currentShape = null;

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
    }

    private void update() {
        if(gameOver || gamePaused || showingLevelComplete || showingCareerComplete || showingEndlessGameOver || showingClassicGameOver || showingCareerGameOver) {
            return;
        }

        // Update shape with dynamic speed for Career and Endless modes
        if (gameMode.equals(GameMode.CAREER) && !gameOver) {
            currentShape.updateWithSpeed(careerManager.getCurrentSpeed());
        } else if (gameMode.equals(GameMode.ENDLESS) && !gameOver) {
            currentShape.updateWithSpeed(endlessManager.getCurrentSpeed());
        } else {
            currentShape.update();
        }
    }


    private void checkGameOver() {
        // Only check if game is not already over
        if (gameOver) {
            return;
        }

        int[][] coords = currentShape.getCoords();
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    if (board[row + currentShape.getY()][col + currentShape.getX()] != null) {
                        gameOver = true;

                        // Show career game over screen
                        if (gameMode.equals(GameMode.CAREER)) {
                            showingCareerGameOver = true;
                        }

                        // Check high score for Classic mode (only once)
                        if (gameMode.equals(GameMode.CLASSIC)) {
                            if (score > classicHighScore) {
                                classicHighScore = score;
                                classicNewHighScore = true;
                            } else {
                                classicNewHighScore = false;
                            }
                            showingClassicGameOver = true;
                        }

                        // Check high score for Endless mode (only once)
                        if (gameMode.equals(GameMode.ENDLESS)) {
                            endlessManager.pause();
                            endlessManager.checkHighScore(score);
                            showingEndlessGameOver = true;
                        }

                        return; // Exit immediately after setting game over
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

        // draw current shape (only if not showing completion screens)
        if (!showingLevelComplete && !showingCareerComplete && currentShape != null) {
            currentShape.render(g);
        }

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
            g.drawImage(menuIcon.getScaledInstance(70, 70, BufferedImage.SCALE_SMOOTH),
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
        g.setFont(fontRegular);
        g.drawString("SCORE", WindowGame.WIDTH - 125, WindowGame.HEIGHT - 600 );
        g.drawString(score + "", WindowGame.WIDTH - 90, WindowGame.HEIGHT - 570);

        // Draw Classic Mode info
        if (gameMode.equals(GameMode.CLASSIC) && !gameOver) {
            g.setFont(fontRegular);
            g.drawString("BEST", WindowGame.WIDTH - 115, WindowGame.HEIGHT - 530);
            g.drawString(classicHighScore + "", WindowGame.WIDTH - 90, WindowGame.HEIGHT - 500);
        }

        // Draw Career Mode info
        if (gameMode.equals(GameMode.CAREER) && !gameOver) {
            g.setFont(fontRegular.deriveFont(21.5f));
            g.drawString("LEVEL " + careerManager.getCurrentLevel(), WindowGame.WIDTH - 125, WindowGame.HEIGHT - 530);
            g.drawString("LINES", WindowGame.WIDTH - 125, WindowGame.HEIGHT - 500);
            g.drawString(careerManager.getProgressText(), WindowGame.WIDTH - 125, WindowGame.HEIGHT - 475);
        }

        // Draw Endless Mode info
        if (gameMode.equals(GameMode.ENDLESS) && !gameOver) {
            g.setFont(fontRegular);
            g.drawString("TIME", WindowGame.WIDTH - 115, WindowGame.HEIGHT - 525);
            g.drawString(endlessManager.getFormattedTime(), WindowGame.WIDTH - 110, WindowGame.HEIGHT - 495);
            g.drawString("BEST", WindowGame.WIDTH - 115, WindowGame.HEIGHT - 450);
            g.drawString(endlessManager.getHighScore() + "", WindowGame.WIDTH - 90, WindowGame.HEIGHT - 420);
        }

        if (gamePaused && !showingLevelComplete && !showingCareerComplete) {
            String gamePausedString = "GAME PAUSED";
            g.setColor(Color.WHITE);
            g.setFont(fontRegular);
            g.drawString(gamePausedString, 50, WindowGame.HEIGHT /2);
        }

        if (showingLevelComplete && gameMode.equals(GameMode.CAREER)) {
            drawLevelComplete(g);
        }

        if (showingCareerComplete && gameMode.equals(GameMode.CAREER)) {
            drawCareerComplete(g);
        }

        if (showingEndlessGameOver && gameMode.equals(GameMode.ENDLESS)) {
            drawEndlessGameOver(g);
        }

        if (showingClassicGameOver && gameMode.equals(GameMode.CLASSIC)) {
            drawClassicGameOver(g);
        }

        if (showingCareerGameOver && gameMode.equals(GameMode.CAREER)) {
            drawCareerGameOver(g);
        }
    }

    private void drawLevelComplete(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);

        // Level complete text
        g.setFont(fontRegular.deriveFont(20f));
        g.setColor(new Color(0, 255, 100));
        String levelText = "LEVEL " + careerManager.getCurrentLevel() + " COMPLETE!";
        FontMetrics fm = g.getFontMetrics();
        int textX = (WindowGame.WIDTH - fm.stringWidth(levelText)) / 2;
        g.drawString(levelText, textX, WindowGame.HEIGHT / 2 - 70);

        // Lines cleared
        g.setFont(fontRegular.deriveFont(20f));
        g.setColor(Color.WHITE);
        String linesText = "Lines Cleared: " + careerManager.getLinesCleared();
        textX = (WindowGame.WIDTH - fm.stringWidth(linesText)) / 2;
        g.drawString(linesText, textX, WindowGame.HEIGHT / 2 - 40);

        // Instructions
        g.setFont(fontRegular.deriveFont(20f));
        g.setColor(new Color(255, 255, 100));
        String enterText = "Press ENTER for Next Level";
        textX = (WindowGame.WIDTH - fm.stringWidth(enterText)) / 2;
        g.drawString(enterText, textX, WindowGame.HEIGHT / 2 + 20);

        g.setColor(new Color(255, 100, 100));
        String escText = "Press ESC to Return to Menu";
        textX = (WindowGame.WIDTH - fm.stringWidth(escText)) / 2;
        g.drawString(escText, textX, WindowGame.HEIGHT / 2 + 50);
    }

    private void drawCareerComplete(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);

        // Victory text
        g.setFont(fontRegular);
        g.setColor(new Color(255, 215, 0));
        String victoryText = "YOU WIN!";
        FontMetrics fm = g.getFontMetrics();
        int textX = (WindowGame.WIDTH - fm.stringWidth(victoryText)) / 2;
        g.drawString(victoryText, textX, WindowGame.HEIGHT / 2 - 60);

        // Congratulations
        g.setFont(fontRegular.deriveFont(20f));
        g.setColor(Color.WHITE);
        String congrats = "Congratulations!";
        fm = g.getFontMetrics();
        textX = (WindowGame.WIDTH - fm.stringWidth(congrats)) / 2;
        g.drawString(congrats, textX, WindowGame.HEIGHT / 2 - 10);

        // Total lines
        String totalLines = "Total Lines Cleared: " + careerManager.getTotalLinesCleared();
        textX = (WindowGame.WIDTH - fm.stringWidth(totalLines)) / 2;
        g.drawString(totalLines, textX, WindowGame.HEIGHT / 2 + 20);

        // Instructions
        g.setFont(fontRegular.deriveFont(20f));
        g.setColor(new Color(255, 255, 100));
        String escText = "Press ESC to Return to Menu";
        textX = (WindowGame.WIDTH - fm.stringWidth(escText)) / 2;
        g.drawString(escText, textX, WindowGame.HEIGHT / 2 + 70);
    }

    private void drawEndlessGameOver(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);

        // Game Over text
        g.setFont(fontRegular);
        g.setColor(Color.WHITE);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int textX = (WindowGame.WIDTH - fm.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, textX, WindowGame.HEIGHT / 2 - 100);

        // New High Score indicator
        if (endlessManager.isNewHighScore()) {
            g.setFont(fontRegular.deriveFont(20f));
            g.setColor(new Color(255, 215, 0));
            String newHighScore = "NEW HIGH SCORE!";
            fm = g.getFontMetrics();
            textX = (WindowGame.WIDTH - fm.stringWidth(newHighScore)) / 2;
            g.drawString(newHighScore, textX, WindowGame.HEIGHT / 2 - 60);
        }

        // Stats
        g.setFont(fontRegular.deriveFont(18f));
        g.setColor(Color.WHITE);
        fm = g.getFontMetrics();

        String scoreText = "Final Score: " + score;
        textX = (WindowGame.WIDTH - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, textX, WindowGame.HEIGHT / 2 - 20);

        String linesText = "Lines Cleared: " + endlessManager.getLinesCleared();
        textX = (WindowGame.WIDTH - fm.stringWidth(linesText)) / 2;
        g.drawString(linesText, textX, WindowGame.HEIGHT / 2 + 10);

        String timeText = "Time: " + endlessManager.getFormattedTime();
        textX = (WindowGame.WIDTH - fm.stringWidth(timeText)) / 2;
        g.drawString(timeText, textX, WindowGame.HEIGHT / 2 + 40);

        // Instructions
        g.setFont(fontRegular.deriveFont(16f));
        g.setColor(new Color(100, 255, 100));
        String enterText = "Press ENTER to Play Again";
        fm = g.getFontMetrics();
        textX = (WindowGame.WIDTH - fm.stringWidth(enterText)) / 2;
        g.drawString(enterText, textX, WindowGame.HEIGHT / 2 + 80);

        g.setColor(new Color(255, 255, 100));
        String escText = "Press ESC to Return to Menu";
        textX = (WindowGame.WIDTH - fm.stringWidth(escText)) / 2;
        g.drawString(escText, textX, WindowGame.HEIGHT / 2 + 110);
    }

    private void drawClassicGameOver(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);

        // Game Over text
        g.setFont(fontRegular);
        g.setColor(Color.WHITE);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int textX = (WindowGame.WIDTH - fm.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, textX, WindowGame.HEIGHT / 2 - 100);

        // New High Score indicator
        if (classicNewHighScore) {
            g.setFont(fontRegular.deriveFont(20f));
            g.setColor(new Color(255, 215, 0));
            String newHighScore = "NEW HIGH SCORE!";
            fm = g.getFontMetrics();
            textX = (WindowGame.WIDTH - fm.stringWidth(newHighScore)) / 2;
            g.drawString(newHighScore, textX, WindowGame.HEIGHT / 2 - 60);
        }

        // Stats
        g.setFont(fontRegular.deriveFont(18f));
        g.setColor(Color.WHITE);
        fm = g.getFontMetrics();

        String scoreText = "Final Score: " + score;
        textX = (WindowGame.WIDTH - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, textX, WindowGame.HEIGHT / 2 - 10);

        String linesText = "Lines Cleared: " + classicLinesCleared;
        textX = (WindowGame.WIDTH - fm.stringWidth(linesText)) / 2;
        g.drawString(linesText, textX, WindowGame.HEIGHT / 2 + 25);

        // Instructions
        g.setFont(fontRegular.deriveFont(16f));
        g.setColor(new Color(100, 255, 100));
        String enterText = "Press ENTER to Play Again";
        fm = g.getFontMetrics();
        textX = (WindowGame.WIDTH - fm.stringWidth(enterText)) / 2;
        g.drawString(enterText, textX, WindowGame.HEIGHT / 2 + 70);

        g.setColor(new Color(255, 255, 100));
        String escText = "Press ESC to Return to Menu";
        textX = (WindowGame.WIDTH - fm.stringWidth(escText)) / 2;
        g.drawString(escText, textX, WindowGame.HEIGHT / 2 + 100);
    }

    private void drawCareerGameOver(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);

        // Game Over text
        g.setFont(fontRegular);
        g.setColor(Color.WHITE);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        int textX = (WindowGame.WIDTH - fm.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, textX, WindowGame.HEIGHT / 2 - 80);

        // Instructions
        g.setFont(fontRegular.deriveFont(16f));
        g.setColor(new Color(100, 255, 100));
        String enterText = "Press ENTER to Restart Level";
        fm = g.getFontMetrics();
        textX = (WindowGame.WIDTH - fm.stringWidth(enterText)) / 2;
        g.drawString(enterText, textX, WindowGame.HEIGHT / 2 - 20);

        g.setColor(new Color(255, 255, 100));
        String escText = "Press ESC to Return to Menu";
        textX = (WindowGame.WIDTH - fm.stringWidth(escText)) / 2;
        g.drawString(escText, textX, WindowGame.HEIGHT / 2 + 10);
    }

    private void checkCareerProgress() {
        if (gameMode.equals(GameMode.CAREER)) {
            if (careerManager.isCareerComplete()) {
                showingCareerComplete = true;
                gamePaused = true;
            } else if (careerManager.isLevelComplete()) {
                showingLevelComplete = true;
                gamePaused = true;
            }
        }
    }

    public void onLinesCleared(int lines) {
        if (gameMode.equals(GameMode.CAREER)) {
            careerManager.addLinesCleared(lines);
            checkCareerProgress();
        } else if (gameMode.equals(GameMode.ENDLESS)) {
            endlessManager.addLinesCleared(lines);
        } else if (gameMode.equals(GameMode.CLASSIC)) {
            classicLinesCleared += lines;
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

        // Handle level complete screen
        if (showingLevelComplete) {
            if (key == KeyEvent.VK_ENTER) {
                startNextLevel();
                gamePaused = false;
            } else if (key == KeyEvent.VK_ESCAPE) {
                // Advance to next level and clear board before returning to menu
                careerManager.advanceToNextLevel();

                // Clear both the active board and saved career board
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board[row].length; col++) {
                        board[row][col] = null;
                        careerBoard[row][col] = null;  // ← Clear saved board too
                    }
                }

                careerShapeCoords = null;
                careerGameOver = false;  // to ensure clean state
                showingLevelComplete = false;
                gamePaused = false;
                windowGame.returnToMenu();
            }
            return;
        }

        // Handle career complete screen
        if (showingCareerComplete) {
            if (key == KeyEvent.VK_ESCAPE) {
                gamePaused = false;
                windowGame.returnToMenu();
            }
            return;
        }

        // Handle endless game over screen
        if (showingEndlessGameOver) {
            if (key == KeyEvent.VK_ENTER) {
                // Play again - reset Endless mode
                endlessScore = 0;
                endlessLinesCleared = 0;
                clearBoard(endlessBoard);
                endlessGameOver = false;
                endlessShapeCoords = null;
                endlessManager.reset();
                showingEndlessGameOver = false;

                score = 0;
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board[row].length; col++) {
                        board[row][col] = null;
                    }
                }

                setNextShape();
                setCurrentShape();
                gameOver = false;
            } else if (key == KeyEvent.VK_ESCAPE) {
                showingEndlessGameOver = false;
                windowGame.returnToMenu();
            }
            return;
        }

        // Handle classic game over screen
        if (showingClassicGameOver) {
            if (key == KeyEvent.VK_ENTER) {
                // Play again - reset Classic mode
                classicScore = 0;
                classicLinesCleared = 0;
                clearBoard(classicBoard);
                classicGameOver = false;
                classicShapeCoords = null;
                showingClassicGameOver = false;

                score = 0;
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board[row].length; col++) {
                        board[row][col] = null;
                    }
                }

                setNextShape();
                setCurrentShape();
                gameOver = false;
            } else if (key == KeyEvent.VK_ESCAPE) {
                showingClassicGameOver = false;
                windowGame.returnToMenu();
            }
            return;
        }

        // Handle career game over screen
        if (showingCareerGameOver) {
            if (key == KeyEvent.VK_ENTER) {
                // Restart current level
                clearBoard(careerBoard);
                int levelCleared = careerManager.getLinesCleared();
                careerManager.addLinesCleared(-levelCleared);
                careerScore = 0;
                careerGameOver = false;
                careerShapeCoords = null;
                showingCareerGameOver = false;

                score = 0;
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board[row].length; col++) {
                        board[row][col] = null;
                    }
                }

                setNextShape();
                setCurrentShape();
                gameOver = false;
            } else if (key == KeyEvent.VK_ESCAPE) {
                showingCareerGameOver = false;
                windowGame.returnToMenu();
            }
            return;
        }

        // Normal game controls
        if (currentShape != null) {
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            if (currentShape != null) {
                currentShape.speedDown();
            }
        }
    }
    public void addScore(int score) {
        this.score += score;
    }

    public void startGame(String mode) {
        // Only save state if we're switching FROM an active game
        if (looper.isRunning()) {
            saveCurrentModeState();
        }

        this.gameMode = mode;

        // Load the state for the new mode
        loadModeState(mode);

        // Clear completion screens when starting
        showingLevelComplete = false;
        showingCareerComplete = false;
        showingEndlessGameOver = false;

        // Only reset Endless mode timer if it's a fresh start (game over or first time)
        if (mode.equals(GameMode.ENDLESS) && (endlessGameOver || endlessStartTime == 0)) {
            endlessManager.reset();
        }

        // Always create next shape
        setNextShape();

        // Only set new current shape if there isn't a saved one
        if (currentShape == null) {
            setCurrentShape();
        }

        gameOver = false;

        // Start the game loop
        if (!looper.isRunning()) {
            looper.start();
        }
    }

    private void saveCurrentModeState() {
        if (gameMode.equals(GameMode.CLASSIC)) {
            classicScore = score;
            classicGameOver = gameOver;
            // Don't reset lines when saving state
            copyBoard(board, classicBoard);
            // Save current falling shape (if exists)
            if (currentShape != null) {
                classicShapeCoords = copyShapeCoords(currentShape.getCoords());
                classicShapeColor = currentShape.getColor();
                classicShapeX = currentShape.getX();
                classicShapeY = currentShape.getY();
            }
        } else if (gameMode.equals(GameMode.ENDLESS)) {
            endlessScore = score;
            endlessGameOver = gameOver;
            copyBoard(board, endlessBoard);
            // Save current falling shape (if exists)
            if (currentShape != null) {
                endlessShapeCoords = copyShapeCoords(currentShape.getCoords());
                endlessShapeColor = currentShape.getColor();
                endlessShapeX = currentShape.getX();
                endlessShapeY = currentShape.getY();
            }
            // Save timer state
            endlessStartTime = endlessManager.startTime;
            endlessTotalPausedDuration = endlessManager.totalPausedDuration;
            endlessLinesCleared = endlessManager.getLinesCleared();
        } else if (gameMode.equals(GameMode.CAREER)) {
            // Career mode state is already in careerManager
            careerScore = score;
            careerGameOver = gameOver;
            copyBoard(board, careerBoard);
            // Save current falling shape (if exists)
            if (currentShape != null) {
                careerShapeCoords = copyShapeCoords(currentShape.getCoords());
                careerShapeColor = currentShape.getColor();
                careerShapeX = currentShape.getX();
                careerShapeY = currentShape.getY();
            }
        }
    }

    private void loadModeState(String mode) {
        if (mode.equals(GameMode.CLASSIC)) {
            // Reset if THIS MODE was over
            if (classicGameOver) {
                classicScore = 0;
                classicLinesCleared = 0;
                clearBoard(classicBoard);
                classicGameOver = false;
                classicShapeCoords = null; // Clear saved shape
                showingClassicGameOver = false;
            }
            score = classicScore;
            copyBoard(classicBoard, board);
            gameOver = classicGameOver;
            gamePaused = false;

            // Restore falling shape if it exists, otherwise set to null
            if (classicShapeCoords != null && !classicGameOver) {
                currentShape = new Shape(classicShapeCoords, this, classicShapeColor);
                currentShape.setX(classicShapeX);
                currentShape.setY(classicShapeY);
            } else {
                currentShape = null;
            }
        } else if (mode.equals(GameMode.ENDLESS)) {
            // Reset if THIS MODE was over
            if (endlessGameOver) {
                endlessScore = 0;
                clearBoard(endlessBoard);
                endlessGameOver = false;
                endlessShapeCoords = null; // Clear saved shape
                endlessManager.reset();
                showingEndlessGameOver = false;
                // Clear saved timer state
                endlessStartTime = 0;
                endlessTotalPausedDuration = 0;
                endlessLinesCleared = 0;
            }
            score = endlessScore;
            copyBoard(endlessBoard, board);
            gameOver = endlessGameOver;
            gamePaused = false;

            // Restore timer state if continuing (not game over)
            if (!endlessGameOver && endlessStartTime > 0) {
                endlessManager.startTime = endlessStartTime;
                endlessManager.totalPausedDuration = endlessTotalPausedDuration;
                endlessManager.linesCleared = endlessLinesCleared;
            }

            // Restore falling shape if it exists, otherwise set to null
            if (endlessShapeCoords != null && !endlessGameOver) {
                currentShape = new Shape(endlessShapeCoords, this, endlessShapeColor);
                currentShape.setX(endlessShapeX);
                currentShape.setY(endlessShapeY);
            } else {
                currentShape = null;
            }
        } else if (mode.equals(GameMode.CAREER)) {
            // Reset career only if it was previously completed
            if (careerManager.isCareerComplete()) {
                careerManager.reset();
                clearBoard(careerBoard);
                careerScore = 0;
                showingLevelComplete = false;
                showingCareerComplete = false;
                gamePaused = false;
                careerGameOver = false;
                careerShapeCoords = null; // Clear saved shape
            }
            // Reset current level if THIS MODE was over (player failed)
            if (careerGameOver) {
                clearBoard(careerBoard);
                // Reset lines cleared for current level only
                int levelCleared = careerManager.getLinesCleared();
                careerManager.addLinesCleared(-levelCleared);
                careerScore = 0;
                careerGameOver = false;
                showingCareerGameOver = false;
                careerShapeCoords = null; // Clear saved shape
            }
            score = careerScore;
            copyBoard(careerBoard, board);
            gameOver = careerGameOver;
            gamePaused = false;

            // Restore falling shape if it exists, otherwise set to null
            if (careerShapeCoords != null && !careerGameOver && !careerManager.isCareerComplete()) {
                currentShape = new Shape(careerShapeCoords, this, careerShapeColor);
                currentShape.setX(careerShapeX);
                currentShape.setY(careerShapeY);
            } else {
                currentShape = null;
            }
        }
    }

    private void copyBoard(Color[][] source, Color[][] destination) {
        for (int row = 0; row < source.length; row++) {
            for (int col = 0; col < source[row].length; col++) {
                destination[row][col] = source[row][col];
            }
        }
    }

    private int[][] copyShapeCoords(int[][] coords) {
        if (coords == null) return null;
        int[][] copy = new int[coords.length][coords[0].length];
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[row].length; col++) {
                copy[row][col] = coords[row][col];
            }
        }
        return copy;
    }

    public void startNextLevel() {
        // Clear the board
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }

        // Advance to next level
        careerManager.advanceToNextLevel();
        showingLevelComplete = false;

        // Start fresh
        setNextShape();
        setCurrentShape();
        gameOver = false;
    }

    public void stopGame() {
        // Save current state before stopping
        saveCurrentModeState();
        looper.stop();
    }

    private void clearBoard(Color[][] boardToClear) {
        for (int row = 0; row < boardToClear.length; row++) {
            for (int col = 0; col < boardToClear[row].length; col++) {
                boardToClear[row][col] = null;
            }
        }
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
            // Don't allow pause during completion screens
            if (showingLevelComplete || showingCareerComplete || showingEndlessGameOver || showingClassicGameOver || showingCareerGameOver) {
                return;
            }
            if (gamePaused) {
                gamePaused = false;
                if (gameMode.equals(GameMode.ENDLESS)) {
                    endlessManager.resume();
                }
            } else {
                gamePaused = true;
                if (gameMode.equals(GameMode.ENDLESS)) {
                    endlessManager.pause();
                }
            }
        }
        if(refreshBounds.contains(mouseX,mouseY)) {
            // Don't allow refresh during completion screens
            if (showingLevelComplete || showingCareerComplete || showingEndlessGameOver || showingClassicGameOver || showingCareerGameOver) {
                return;
            }
            // Reset only the current mode's state
            if (gameMode.equals(GameMode.CAREER)) {
                // Reset only the current level, not the entire career
                int levelCleared = careerManager.getLinesCleared();
                careerManager.addLinesCleared(-levelCleared); // Reset current level lines to 0
                careerScore = 0;

                showingLevelComplete = false;
                showingCareerComplete = false;
                showingCareerGameOver = false;
                clearBoard(careerBoard);
                careerGameOver = false;
                careerShapeCoords = null; // Clear saved shape
            } else if (gameMode.equals(GameMode.CLASSIC)) {
                classicScore = 0;
                classicLinesCleared = 0;
                clearBoard(classicBoard);
                classicGameOver = false;
                classicShapeCoords = null; // Clear saved shape
                showingClassicGameOver = false;
            } else if (gameMode.equals(GameMode.ENDLESS)) {
                endlessScore = 0;
                clearBoard(endlessBoard);
                endlessGameOver = false;
                endlessShapeCoords = null; // Clear saved shape
                endlessManager.reset();
                showingEndlessGameOver = false;
            }

            score = 0;
            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    board[row][col] = null;
                }
            }

            setNextShape();
            setCurrentShape();
            gameOver = false;
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
            // Don't allow menu button during completion screens - use ESC key instead
            if (showingLevelComplete || showingCareerComplete || showingEndlessGameOver || showingClassicGameOver || showingCareerGameOver) {
                return;
            }
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