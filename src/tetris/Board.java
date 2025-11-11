package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements KeyListener {
    private int score = 0;


    private static int FPS = 60;
    private static int delay = 1000 / FPS;

    public static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private Timer looper;
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private boolean gameOver = false;

    private Random random;

    //Array of Colors
    private Color[] colors = {Color.decode("#ed1c24"),Color.decode("#ff7f27"),Color.decode("#fff200"),
            Color.decode("#22b14c"),Color.decode("#00a2e8"),Color.decode("#a349a4"),Color.decode("#3f48cc")};

    //Array of Shape
    private Shape[] shapes = new Shape[8];

    private Shape currentShape;

    public Board() {
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
    private void update(){
        currentShape.update();
    }


    private void checkGameOver() {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[0][col] != null) {
                gameOver = true;
                return;
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


        if (gameOver) {
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.setColor(Color.white);
            g.drawString("GAME OVER", 50, 300);
        }
    }

    public void setCurrentShape() {
        // Pick a random shape type index
        int shapeIndex = random.nextInt(shapes.length);

        // Get the shape pattern from shapes array at that index
        int[][] shapePattern = shapes[shapeIndex].getPattern();

        // Assign a new Shape with the same pattern but a new random color
        currentShape = new Shape(shapePattern, this, colors[random.nextInt(colors.length)]);

        currentShape.reset();
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
    public void addScore() {
        score++;
    }

}