package tetris;

/*test*/import java.awt.Color;
import javax.swing.JFrame;

public class Core {
    public static final int WIDTH = 445, HEIGHT = 629;

    private Board board;
    private JFrame window;

    public Core() {
        window = new JFrame("Tetris");
        window.setSize(WIDTH, HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        board = new Board();
        window.add(board);
    }

    public static void main(String[] args) {
        new Core();
    }
}
