package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel {
    private Timer looper;

    public Board() {
        looper = new Timer(1000, new ActionListener() {
            int n = 0;
            @Override
            public void  actionPerformed(ActionEvent e){
                System.out.println(n++);
            }
        });
        looper.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        g.drawRect(10, 10, 200, 570);
    }
}