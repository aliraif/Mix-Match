package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements KeyListener {
    private int score = 0;


    private static int FPS = 60;
    private static int delay = 1000 / FPS;

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private Timer looper;
    private Color[][] board = new Color[BOARD_WIDTH][BOARD_HEIGHT];

    private Color[][] shape = {
            {Color.RED, Color.RED, Color.RED},
            {null, Color.RED, null},
    };

    private int x = 4, y = 0;
    private int normal = 600;
    private int fast = 50;
    private int delayTimeForMovement = normal;
    private long beginTime;

    private int deltaX = 0;
    private boolean collision = false;
    private boolean gameOver = false;

    public Board() {
        looper = new Timer(delay, new ActionListener() {
            int n = 0;
            @Override
            public void  actionPerformed(ActionEvent e){
                if (gameOver) {
                    repaint();
                    return;
                }
                if(collision) {
                    placeShape();
                    return;
                }


                // check moving horizontal
                if(!(x + deltaX + shape[0].length > 10) && !(x + deltaX < 0)) {
                    x+= deltaX;
                }
                deltaX = 0;

                if(System.currentTimeMillis() - beginTime > delayTimeForMovement) {
                    if(!(y + 1 + shape.length > BOARD_HEIGHT)) {
                        y++;
                    } else {
                        collision = true;
                    }
                    beginTime = System.currentTimeMillis();
                } 
                repaint();
            }
        });
        looper.start();
    }
    private void placeShape() {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != null) {
                    board[y + row][x + col] = shape[row][col];
                }
            }
        }

        checkGameOver();
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
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), getHeight());

        //draw the shape
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != null) {
                    g.setColor(shape[row][col]);
                    g.fillRect(col * BLOCK_SIZE + x * BLOCK_SIZE, row * BLOCK_SIZE + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE );
                }
            }
        }

        //draw the board
        g.setColor(Color.white);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            g.drawLine(0, BLOCK_SIZE * row,BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            g.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
        }
        if (gameOver) {
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.setColor(Color.white);
            g.drawString("GAME OVER", 50, 300);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            delayTimeForMovement = fast;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            deltaX = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            deltaX = -1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) return;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            delayTimeForMovement = normal;
        }
    }
    public void addScore() {
        score++;
    }

}