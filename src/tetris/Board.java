package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private Timer looper;
    private Color[][] board = new Color[BOARD_WIDTH][BOARD_HEIGHT];

    private Color[][] shape = {
            {Color.RED, Color.RED, Color.RED},
            {null, Color.RED, null},
    };
    private boolean gameOver = false;

    public boolean canMove(int deltaX, int deltaY) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != null) {
                    int newX = col + deltaX;
                    int newY = row + deltaY;

                    if (newX < 0 || newX >= BOARD_WIDTH || newY < 0 || newY >= BOARD_HEIGHT) {
                        return false;
                    }

                    if (board[newX][newY] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Board() {
        looper = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) return;

                if (canMove(0, 1)) {
                    moveShapeDown();
                } else {
                    fixShape();
                    spawnNewShape();
                }
                repaint();
            }
        });
        looper.start();
    }

    private void moveShapeDown() {
        Color[][] newShape = new Color[shape.length][shape[0].length];
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                newShape[r][c] = shape[r][c];
            }
        }
        shape = newShape;
    }

    private void fixShape() {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != null) {
                    board[c][r] = shape[r][c];
                }
            }
        }
    }
    private void spawnNewShape() {
        shape = new Color[][] {
                {Color.RED, Color.RED, Color.RED},
                {null, Color.RED, null},
        };

        if (!canMove(0, 0)) {
            gameOver = true;
            looper.stop();
            System.out.println("GAME OVER!");
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != null) {
                    g.setColor(shape[row][col]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }


        g.setColor(Color.white);
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        }
        for (int col = 0; col < BOARD_WIDTH; col++) {
            g.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
        }

        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[col][row] != null) {
                    g.setColor(board[col][row]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.drawString("GAME OVER", getWidth() / 4, getHeight() / 2);
        }
    }
}