package tetris;

import javax.swing.JFrame;
import audio.SoundManager;

public class WindowGame {
    public static final int WIDTH = 445, HEIGHT = 629;

    private Board board;
    private MainMenu mainMenu;
    private JFrame window;

    public WindowGame() {

        window = new JFrame("Tetris");
        window.setSize(WIDTH, HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);

        board = new Board(this);
        mainMenu = new MainMenu(this);
        //title = new Title(this);

        window.add(mainMenu);
        window.addKeyListener(mainMenu);
        window.setVisible(true);
    }

    public void startTetris() {
        window.remove(mainMenu);
        window.removeKeyListener(mainMenu);

        window.add(board);
        window.addKeyListener(board);
        window.addMouseMotionListener(board);
        window.addMouseListener(board);

        window.revalidate();
        window.repaint();

        board.requestFocusInWindow();
        board.startGame();
    }

    public void returnToMenu() {
        window.remove(board);
        window.removeKeyListener(board);
        window.removeMouseMotionListener(board);
        window.removeMouseListener(board);

        window.add(mainMenu);
        window.addKeyListener(mainMenu);

        board.stopGame();
        mainMenu.requestFocusInWindow();
        window.revalidate();
        window.repaint();
    }

    public static void main(String[] args) {
        new WindowGame();
        SoundManager.stopAll();
        //SoundManager.playLoop("sfx/HoldOnTight.wav");
        SoundManager.playLoop("sfx/Tetris.wav");
    }

}
